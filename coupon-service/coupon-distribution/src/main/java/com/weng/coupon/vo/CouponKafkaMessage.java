package com.weng.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {
    /** 优惠券状态 */
    private Integer status;

    /** Coupon 主键 */
    private List<Integer> ids;
}
