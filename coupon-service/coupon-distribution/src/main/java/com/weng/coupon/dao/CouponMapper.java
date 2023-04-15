package com.weng.coupon.dao;

import com.weng.coupon.constants.CouponStatus;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.handler.CouponStatusTypeHandler;
import com.weng.coupon.handler.JodaTimeTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface CouponMapper {

    @Select("select * from coupon where user_id=#{userId} and status=#{status.code}")
    @Results(id="CouponMap", value = {
            @Result(property = "assignTime", column = "assign_time", typeHandler = JodaTimeTypeHandler.class),
            @Result(property = "status", column = "status", typeHandler = CouponStatusTypeHandler.class)
    })
    /**
     * <h2>根据 userId + 状态寻找优惠券记录</h2>
     * where userId = ... and status = ...
     * */
    List<Coupon> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") CouponStatus status);
}
