package com.weng.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponCategory {
    MANJIAN("优惠券","001"),
    ZHEKOU("折扣券", "002"),
    LIJIAN("立减券", "003");


    //优惠券描述
    private String description;
    //优惠券编码
    private String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(e -> code.equals(e.code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exist"));
    }


}
