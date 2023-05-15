package com.weng.coupon.utils;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.weng.coupon.constant.CouponStatus;
import com.weng.coupon.constant.PeriodType;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {
    private List<Coupon> usableCoupons;
    private List<Coupon> usedCoupons;
    private List<Coupon> expiredCoupons;

    public static CouponClassify classify(List<Coupon> coupons) {
        List<Coupon> used = new ArrayList<>();
        List<Coupon> usable = new ArrayList<>();
        List<Coupon> expired = new ArrayList<>();
        coupons.forEach(coupon -> {
            switch (coupon.getStatus()) {
                case USED:
                    used.add(coupon);
                    break;
                case EXPIRED:
                    expired.add(coupon);
                    break;
                case USABLE:
                    Boolean isExpire = false;
                    TemplateRule.Expiration expiration = coupon.getTemplateSDK().getRule().getExpiration();
                    if (expiration.getPeriod() == PeriodType.REGULAR.getCode()) {
                        // 固定过期日期处理
                        if (expiration.getDeadline() <= new DateTime().getMillis()) {
                            // 过期
                            isExpire = true;
                        }
                    } else {
                        // 灵活日期处理
                        if (coupon.getAssignTime().plusMillis(expiration.getGap()).isBeforeNow()) {
                            // 过期
                            isExpire = false;
                        }
                    }
                    if (isExpire) {
                        coupon.setStatus(CouponStatus.EXPIRED);
                        expired.add(coupon);
                        break;
                    }
                    usable.add(coupon);
                    break;
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
