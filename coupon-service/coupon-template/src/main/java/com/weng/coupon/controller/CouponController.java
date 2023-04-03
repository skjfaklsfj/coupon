package com.weng.coupon.controller;

import com.weng.coupon.annotation.IgnoreResponseAdvice;
import com.weng.coupon.dao.CouponTemplateMapper;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.service.AsyncService;
import com.weng.coupon.vo.TemplateRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController {
    @Autowired
    private CouponTemplateMapper mapper;
    @Autowired
    private AsyncService asyncService;
    @GetMapping("test")
    public CouponTemplate test() throws Exception {
        CouponTemplate couponTemplate = mapper.findByName("abc");
        asyncService.asyncConstructCouponByTemplate(couponTemplate);
        couponTemplate = mapper.findByName("sd");
        asyncService.asyncConstructCouponByTemplate(couponTemplate);
        return couponTemplate;
    }
}
