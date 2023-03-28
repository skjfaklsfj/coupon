package com.weng.coupon.controller;

import com.weng.coupon.annotation.IgnoreResponseAdvice;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.vo.TemplateRule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@IgnoreResponseAdvice
public class CouponController {
    @GetMapping("test")
    public CouponTemplate test() throws Exception {
        TemplateRule.Expiration expiration = new TemplateRule.Expiration(5, 5, 4L);
        TemplateRule.Usage usage = new TemplateRule.Usage("浙江", "衢州", "生鲜");
        TemplateRule.Discount discount = new TemplateRule.Discount(50, 400);
        TemplateRule templateRule = new TemplateRule(expiration, discount, 100, usage, "100");
        CouponTemplate couponTemplate = new CouponTemplate("abc", "dcf", "你好", "001", 1, 1, 100L, 1, templateRule);
        couponTemplate.setId(1);
        return couponTemplate;
    }
}
