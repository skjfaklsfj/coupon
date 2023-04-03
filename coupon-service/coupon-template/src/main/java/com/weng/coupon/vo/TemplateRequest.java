package com.weng.coupon.vo;

import com.weng.coupon.constant.CouponCategory;
import com.weng.coupon.constant.DistributeTarget;
import com.weng.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    private String name;
    private String logo;
    private String desc;
    private String category;
    private Integer productLine;
    private Integer count;
    private Long userId;
    private Integer target;
    private TemplateRule rule;
    public boolean validate() {
        boolean stringValid = StringUtils.isEmpty(name) && StringUtils.isEmpty(logo) && StringUtils.isEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category) && null != ProductLine.of(productLine) && null != DistributeTarget.of(target);
        boolean numValid = count != null && count > 0 && userId != null && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }
}
