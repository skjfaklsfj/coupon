package com.weng.coupon.deserializer;

import com.google.gson.*;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.vo.CouponTemplateSDK;
import com.weng.coupon.vo.TemplateRule;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class CouponDeserializer implements JsonDeserializer<Coupon> {
    @Override
    public Coupon deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Coupon coupon = new Coupon();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        coupon.setId(jsonObject.get("id").getAsInt());
        coupon.setTemplateId(jsonObject.get("templateId").getAsInt());
        coupon.setUserId(jsonObject.get("userId").getAsLong());
        coupon.setCouponCode(jsonObject.get("couponCode").getAsString());
        coupon.setAssignTime(new DateTime(jsonObject.get("assignTime").getAsString()));
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        coupon.setTemplateSDK(templateSDK);
        templateSDK.setName(jsonObject.get("name").getAsString());
        templateSDK.setLogo(jsonObject.get("logo").getAsString());
        templateSDK.setDesc(jsonObject.get("desc").getAsString());
        TemplateRule templateRule = new TemplateRule();
        templateSDK.setRule(templateRule);
        templateRule.setExpiration(new Gson().fromJson(jsonObject.get("expiration").getAsString(), TemplateRule.Expiration.class));
        templateRule.setDiscount(new Gson().fromJson(jsonObject.get("discount").getAsString(), TemplateRule.Discount.class));
        templateRule.setUsage(new Gson().fromJson(jsonObject.get("usage").getAsString(), TemplateRule.Usage.class));
        return coupon;
    }
}
