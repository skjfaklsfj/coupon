package com.weng.coupon.Entity;

import com.weng.coupon.constant.CouponCategory;
import com.weng.coupon.constant.DistributeTarget;
import com.weng.coupon.constant.ProductLine;
import com.weng.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplate {
    /** 自增主键 */
    private Integer id;
    /** 是否是可用状态 */
    private Boolean available;
    /** 是否过期 */
    private Boolean expired;
    /** 优惠券名称 */
    private String name;
    /** 优惠券 logo */
    private String logo;
    /** 优惠券描述 */
    private String desc;
    /** 优惠券分类 */
    private CouponCategory category;
    /** 产品线 */
    private ProductLine productLine;
    /** 总数 */
    private Integer count;
    /** 创建用户 */
    private Long userId;
    /** 优惠券模板的编码 */
    private String key;
    /** 目标用户 */
    private DistributeTarget target;
    /** 优惠券规则 */
    private TemplateRule rule;

    public CouponTemplate(String name, String logo, String desc, String category, Integer productLine, Integer count, Long userId, Integer target, TemplateRule templateRule) {
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        this.target = DistributeTarget.of(target);
        this.rule = templateRule;
        // 优惠券模板唯一编码 = 4(产品线和类型) + 8(日期: 20190101) + id(扩充为4位)
        this.key = productLine.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

}
