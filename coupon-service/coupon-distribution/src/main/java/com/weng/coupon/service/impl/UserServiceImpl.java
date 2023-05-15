package com.weng.coupon.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.weng.coupon.constant.Constant;
import com.weng.coupon.constant.CouponStatus;
import com.weng.coupon.dao.CouponMapper;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.feign.SettlementClient;
import com.weng.coupon.feign.TemplateClient;
import com.weng.coupon.service.KafkaService;
import com.weng.coupon.service.RedisService;
import com.weng.coupon.service.UserService;
import com.weng.coupon.utils.CouponClassify;
import com.weng.coupon.vo.AcquireTemplateRequest;
import com.weng.coupon.vo.CouponKafkaMessage;
import com.weng.coupon.vo.CouponTemplateSDK;
import com.weng.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private TemplateClient templateClient;
    @Autowired
    private SettlementClient settlementClient;

    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> cachedCoupons = redisService.getCachedCoupons(userId, status);
        List<Coupon> finCoupons = new ArrayList<>();
        if (cachedCoupons.isEmpty()) {
            log.debug("coupon cache is empty, get coupon from db: {}, {}",
                    userId, status);
            List<Coupon> dbCoupons = couponMapper.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            // 如果数据库中没有记录, 直接返回就可以, Cache 中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon: {}, {}", userId, status);
                return dbCoupons;
            }
            // 填充 dbCoupons的 templateSDK 字段
            Map<Integer, CouponTemplateSDK> templateSDKMap = templateClient.findIds2TemplateSDK(dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toSet())).getData();
            dbCoupons.forEach(coupon -> coupon.setTemplateSDK(templateSDKMap.get(coupon.getTemplateId())));
            redisService.addCouponToCache(userId, dbCoupons, status);
            finCoupons = dbCoupons;
        } else {
            log.debug("coupon cache is not empty: {}, {}", userId, status);
            finCoupons = cachedCoupons;
        }
        // 将无效优惠券剔除
        finCoupons = finCoupons.stream().filter(coupon -> coupon.getId() != -1).collect(Collectors.toList());
        // 如果当前获取的是可用优惠券, 还需要做对已过期优惠券的延迟处理
        if (CouponStatus.USABLE.getCode().equals(Integer.valueOf(status))) {
            CouponClassify couponClassify = CouponClassify.classify(finCoupons);
            // 如果已过期状态不为空, 需要做延迟处理
            if (!couponClassify.getExpiredCoupons().isEmpty()) {
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: " +
                        "{}, {}", userId, status);
                redisService.addCouponToCache(userId, couponClassify.getExpiredCoupons(), CouponStatus.EXPIRED.getCode());
                kafkaTemplate.send(Constant.TOPIC, new Gson().toJson(new CouponKafkaMessage(CouponStatus.EXPIRED.getCode(),
                        couponClassify.getExpiredCoupons().stream().map(Coupon::getId).collect(Collectors.toList()))));
            }
            return couponClassify.getUsableCoupons();
        }
        return finCoupons;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
        log.debug("Find All Template(From TemplateClient) Count: {}",
                templateSDKS.size());
        // 过滤过期的优惠券模板
        templateSDKS = templateSDKS.stream().filter(e -> e.getRule().getExpiration().getDeadline() > new DateTime().getMillis()).collect(Collectors.toList());
        log.info("Find Usable Template Count: {}", templateSDKS.size());
        // key 是 TemplateId
        // value 中的 left 是 Template limitation, right 是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>();
        templateSDKS.forEach(templateSDK -> {
            limit2Template.put(templateSDK.getId(), Pair.of(templateSDK.getRule().getLimitation(), templateSDK));
        });

        List<CouponTemplateSDK> result = new ArrayList<>();
        List<Coupon> couponList = findCouponsByStatus(userId, CouponStatus.USABLE.getCode());
        log.debug("Current User Has Usable Coupons: {}, {}", userId,
                couponList.size());

        Map<Integer, List<Coupon>> templateId2Coupons = couponList.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));
        limit2Template.forEach((k, v) -> {
            List<Coupon> coupons = templateId2Coupons.get(k);
            if (coupons == null || coupons.size() < v.getLeft()) {
                result.add(v.getRight());
            }
        });
        return result;
    }

    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(Lists.newArrayList(request.getTemplateSDK().getId())).getData();
        CouponTemplateSDK couponTemplateSDK = id2Template.get(request.getTemplateSDK().getId());
        // 优惠券模板是需要存在的
        if (couponTemplateSDK == null) {
            log.error("Can Not Acquire Template From TemplateClient: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        } else if (couponTemplateSDK.getRule().getExpiration().getDeadline() <= new DateTime().getMillis()) {
            // 优惠券未过期
            log.error("Template is expired: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Template is expired");
        }

        // 用户是否可以领取这张优惠券
        List<Coupon> usableCoupons = findCouponsByStatus(request.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> templateId2Coupons = usableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (templateId2Coupons.containsKey(couponTemplateSDK.getId()) && templateId2Coupons.get(couponTemplateSDK.getId()).size() > couponTemplateSDK.getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }

        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(couponTemplateSDK.getId());
        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }
        Coupon coupon = new Coupon(couponTemplateSDK.getId(), request.getUserId(), couponCode, CouponStatus.USABLE);
        // 写入db
        coupon = couponMapper.addCoupon(coupon);
        coupon.setTemplateSDK(couponTemplateSDK);
        // 放入缓存中
        redisService.addCouponToCache(request.getUserId(), Lists.newArrayList(coupon), CouponStatus.USABLE.getCode());
        return coupon;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
