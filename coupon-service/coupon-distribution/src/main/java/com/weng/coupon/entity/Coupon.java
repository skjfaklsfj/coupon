package com.weng.coupon.entity;

import com.weng.coupon.constant.CouponStatus;
import com.weng.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    /** 自增主键 */
    private Integer id;

    /** 关联优惠券模板的主键(逻辑外键) */
    private Integer templateId;

    /** 领取用户 */
    private Long userId;

    /** 优惠券码 */
    private String couponCode;

    /** 领取时间 */
    private DateTime assignTime;

    /** 优惠券状态 */
    private CouponStatus status;

    /** 用户优惠券对应的模板信息 */
    private CouponTemplateSDK templateSDK;

    /**
     * <h2>返回一个无效的 Coupon 对象</h2>
     * */
    public static Coupon invalidCoupon() {

        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * <h2>构造优惠券</h2>
     * */
    public Coupon(Integer templateId, Long userId, String couponCode,
                  CouponStatus status) {

        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}
