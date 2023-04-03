package com.weng.coupon.service;

import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.vo.CouponTemplateSDK;
import com.weng.coupon.vo.TemplateRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BuildTemplateService {
    /**
     * <h2>创建优惠券模板</h2>
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
