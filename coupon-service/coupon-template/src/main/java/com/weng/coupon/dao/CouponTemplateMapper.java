package com.weng.coupon.dao;

import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.handler.JodaTimeTypeHandler;
import com.weng.coupon.handler.TemplateRuleTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;

import java.util.List;

@Mapper
public interface CouponTemplateMapper {

    /**
     * <h2>根据模板名称查询模板</h2>
     * where name = ...
     * */
    @Select("select * from coupon_template where name = #{name}")
    @Results(id = "CouponTemplateMap", value = {
            @Result(property = "desc", column = "intro"),
            @Result(property = "category", column = "category", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "productLine", column = "product_line", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "count", column = "coupon_count"),
            @Result(property = "createTime", column = "create_time", typeHandler = JodaTimeTypeHandler.class),
            @Result(property = "key", column = "template_key"),
            @Result(property = "target", column = "target", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "rule", column = "rule", typeHandler = TemplateRuleTypeHandler.class)
    })
    public CouponTemplate findByName(@Param("name") String name);
//    /**
//     * <h2>根据 available 和 expired 标记查找模板记录</h2>
//     * where available = ... and expired = ...
//     * */
//    public List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);
//    /**
//     * <h2>根据 expired 标记查找模板记录</h2>
//     * where expired = ...
//     * */
//    public List<CouponTemplate> findAllByExpired(Boolean expired);
}
