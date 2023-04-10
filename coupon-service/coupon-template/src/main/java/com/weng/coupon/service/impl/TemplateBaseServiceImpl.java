package com.weng.coupon.service.impl;

import com.weng.coupon.dao.CouponTemplateMapper;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.service.TemplateBaseService;
import com.weng.coupon.vo.CouponTemplateSDK;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TemplateBaseServiceImpl implements TemplateBaseService {
    @Autowired
    private CouponTemplateMapper couponTemplateMapper;
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        CouponTemplate couponTemplate = couponTemplateMapper.findById(id);
        if (null == couponTemplate) {
            throw new CouponException("couponTemplate not exists:" + id);
        }
        return couponTemplate;
    }

    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> couponTemplates = couponTemplateMapper.findAllByAvailableAndExpired(true, false);
        //todo:过期字段更新是定时任务，需要再次校验是否过期
        return couponTemplates.stream().map(this::couponTemplate2CouponTemplateSDK).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        return ids.stream().map(this.couponTemplateMapper::findById).filter(Objects::nonNull).map(this::couponTemplate2CouponTemplateSDK)
                .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }

    private CouponTemplateSDK couponTemplate2CouponTemplateSDK(CouponTemplate template) {
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(),  // 并不是拼装好的 Template Key
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
