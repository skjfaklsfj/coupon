package com.weng.coupon.service;

import com.weng.coupon.entity.Coupon;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.vo.AcquireTemplateRequest;
import com.weng.coupon.vo.CouponTemplateSDK;
import com.weng.coupon.vo.SettlementInfo;

import java.util.List;

public interface UserService {
    /**
     * <h2>根据用户 id 和状态查询优惠券记录</h2>
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status)
            throws CouponException;

    /**
     * <h2>根据用户 id 查找当前可以领取的优惠券模板</h2>
     *
     * @param userId 用户 id
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId)
            throws CouponException;

    /**
     * <h2>用户领取优惠券</h2>
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    Coupon acquireTemplate(AcquireTemplateRequest request)
            throws CouponException;

    /**
     * <h2>结算(核销)优惠券</h2>
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
