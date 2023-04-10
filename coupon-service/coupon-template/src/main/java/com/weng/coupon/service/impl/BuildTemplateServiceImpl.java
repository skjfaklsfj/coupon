package com.weng.coupon.service.impl;

import com.weng.coupon.dao.CouponTemplateMapper;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.service.AsyncService;
import com.weng.coupon.service.BuildTemplateService;
import com.weng.coupon.vo.TemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class BuildTemplateServiceImpl implements BuildTemplateService {
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private CouponTemplateMapper couponTemplateMapper;

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        if (!request.validate()) {
            throw new CouponException("BuildTemplate param isn't valid");
        }
        if (null != couponTemplateMapper.findByName(request.getName())) {
            throw new CouponException("Exist same name couponTemplate");
        }
        CouponTemplate couponTemplate = templateRequest2CouponTemplate(request);
        couponTemplateMapper.addNewCouponTemplate(couponTemplate);
        asyncService.asyncConstructCouponByTemplate(couponTemplate);
        return couponTemplate;
    }

    private CouponTemplate templateRequest2CouponTemplate(TemplateRequest request) {
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }


}
