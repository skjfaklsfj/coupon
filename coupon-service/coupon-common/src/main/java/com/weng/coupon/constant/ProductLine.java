package com.weng.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductLine {
    DABAO("大宝", 1),
    DAMAO("大猫", 2);
    /** 产品线描述 */
    private String description;

    /** 产品线编码 */
    private Integer code;

    public static ProductLine of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(e -> code.equals(e.code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exist"));
    }

}
