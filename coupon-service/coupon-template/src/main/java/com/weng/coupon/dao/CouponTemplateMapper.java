package com.weng.coupon.dao;

import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.handler.JodaTimeTypeHandler;
import com.weng.coupon.handler.TemplateRuleTypeHandler;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

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
    /**
     * <h2>根据 available 和 expired 标记查找模板记录</h2>
     * where available = ... and expired = ...
     * */
    @Select("select * from coupon_template where available = #{available} and expired = #{expired}")
    @ResultMap("CouponTemplateMap")
    public List<CouponTemplate> findAllByAvailableAndExpired(@Param("available") Boolean available, @Param("expired") Boolean expired);
    /**
     * <h2>根据 expired 标记查找模板记录</h2>
     * where expired = ...
     * */
    @Select("select  * from coupon_template where expired = #{expired}")
    @ResultMap("CouponTemplateMap")
    public List<CouponTemplate> findAllByExpired(@Param("expired") Boolean expired);

    @Insert("insert into coupon_template(available, expired, name, logo, intro, category, product_line, coupon_count, create_time, user_id, template_key, target, rule) values" +
            "(#{available}, #{expired}, #{name}, #{logo}, #{desc}, #{category, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}, #{productLine, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}," +
            "#{count}, #{createTime, typeHandler=com.weng.coupon.handler.JodaTimeTypeHandler}, #{userId}, #{key}, #{target, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}, #{rule, typeHandler=com.weng.coupon.handler.TemplateRuleTypeHandler})")
    @SelectKey(statement = "select last_insert_id()", keyProperty = "id", before = false, resultType = Integer.class)
    public int addNewCouponTemplate(CouponTemplate couponTemplate);

    @Update("update coupon_template set available=#{available}, expired=#{expired}, name=#{name}, logo=#{logo}, intro=#{desc}, category=#{category, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}," +
            "product_line=#{productLine, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}, coupon_count=#{count}, template_key=#{key}, target=#{target, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}," +
            "rule=#{rule, typeHandler=com.weng.coupon.handler.TemplateRuleTypeHandler} where id=#{id}")
    public int updateCouponTemplate(CouponTemplate couponTemplate);

    @Select("select * from coupon_template where id = #{id}")
    @ResultMap("CouponTemplateMap")
    public CouponTemplate findById(@Param("id") Integer id);

    @Update({
            "<script>",
            "<foreach collection='couponTemplates' item='item' separator=';'>",
            "update coupon_template set available=#{item.available}, expired=#{item.expired}, name=#{item.name}, logo=#{item.logo}, intro=#{item.desc}, category=#{item.category, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}," +
                    "product_line=#{item.productLine, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}, coupon_count=#{item.count}, template_key=#{item.key}, target=#{item.target, typeHandler=org.apache.ibatis.type.EnumOrdinalTypeHandler}," +
                    "rule=#{item.rule, typeHandler=com.weng.coupon.handler.TemplateRuleTypeHandler} where id=#{item.id}",
            "</foreach>",
            "<script>"
            })
    public int updateCouponTemplates(@Param("couponTemplates") List<CouponTemplate> couponTemplateList);
}