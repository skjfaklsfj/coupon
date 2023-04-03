package com.weng.coupon.service.impl;

import com.weng.coupon.constants.Constant;
import com.weng.coupon.dao.CouponTemplateMapper;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {
    @Autowired
    private CouponTemplateMapper couponTemplateMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Set<String> couponCodes = buildCouponCodes(template);
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode To Redis:{}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        couponTemplateMapper.updateCouponTemplate(template);
        //todo:可以添加发送短信或邮件通知模板已经可用
    }

    /**
     * <h2>构造优惠券码</h2>
     * 优惠券码(对应于每一张优惠券, 18位)
     *  前四位: 产品线 + 类型
     *  中间六位: 日期随机(190101)
     *  后八位: 0 ~ 9 随机数构成
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与 template.count 相同个数的优惠券码
     * */
    private Set<String> buildCouponCodes(CouponTemplate template) {
        Set<String> set = new HashSet<>();
        String prefix = template.getProductLine().getCode() + template.getCategory().getCode() + new SimpleDateFormat("yyMMdd").format(template.getCreateTime().toDate());
        while (set.size() < template.getCount()) {
            set.add(prefix + RandomStringUtils.randomNumeric(8));
        }
        return set;
    }
}
