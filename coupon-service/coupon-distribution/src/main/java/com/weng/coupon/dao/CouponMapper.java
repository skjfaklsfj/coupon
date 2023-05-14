package com.weng.coupon.dao;

import com.weng.coupon.constant.CouponStatus;
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


    @Select({
            "<script>" +
                    "select * from coupon where id in" +
                    "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
                    "#{id}" +
                    "</foreach>" +
                    "and status=#{status.code}" +
            "</script>"})
    @ResultMap("CouponMap")
    List<Coupon> findAllByIdsAndStatus(@Param("ids") List<Integer> ids, @Param("status") CouponStatus status);

    @Update({
            "<script>" +
                    "<foreach collection='coupons' item='coupon' separator=';'>" +
                        "update coupon " +
                        "set template_id=#{coupon.templateId}, user_id=#{coupon.userId}, coupon_code=#{coupon.couponCode}, assign_time=#{coupon.assignTime, typeHandler=com.weng.coupon.handler.JodaTimeTypeHandler}, status=#{coupon.status, typeHandler=com.weng.coupon.handler.CouponStatusTypeHandler} " +
                        "where id=#{coupon.id}" +
                    "</foreach>" +
            "</script>"
    })
    Integer saveAll(@Param("coupons") List<Coupon> coupons);
}
