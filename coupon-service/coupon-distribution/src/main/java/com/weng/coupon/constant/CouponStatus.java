package com.weng.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatus {
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);
    /** 优惠券状态描述信息 */
    private String description;

    /** 优惠券状态编码 */
    private Integer code;

    /**
     * <h2>根据 code 获取到 CouponStatus</h2>
     * */
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(e -> e.code.equals(code)).findAny().orElseThrow(() -> new IllegalArgumentException(code + "not exist"));
    }
}
