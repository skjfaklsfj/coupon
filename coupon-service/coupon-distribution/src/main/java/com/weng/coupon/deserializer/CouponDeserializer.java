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
        if (jsonObject.get("templateId") != null)
            coupon.setTemplateId(jsonObject.get("templateId").getAsInt());
        if (jsonObject.get("userId") != null)
            coupon.setUserId(jsonObject.get("userId").getAsLong());
        if (jsonObject.get("couponCode") != null)
            coupon.setCouponCode(jsonObject.get("couponCode").getAsString());
        if (jsonObject.get("assignTime") != null)
            coupon.setAssignTime(new DateTime(jsonObject.get("assignTime").getAsString()));
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        coupon.setTemplateSDK(templateSDK);
        if (jsonObject.get("name") != null)
            templateSDK.setName(jsonObject.get("name").getAsString());
        if (jsonObject.get("logo") != null)
            templateSDK.setLogo(jsonObject.get("logo").getAsString());
        if (jsonObject.get("desc") != null)
            templateSDK.setDesc(jsonObject.get("desc").getAsString());
        TemplateRule templateRule = new TemplateRule();
        templateSDK.setRule(templateRule);
        if (jsonObject.get("expiration") != null)
            templateRule.setExpiration(new Gson().fromJson(jsonObject.get("expiration").getAsString(), TemplateRule.Expiration.class));
        if (jsonObject.get("discount") != null)
            templateRule.setDiscount(new Gson().fromJson(jsonObject.get("discount").getAsString(), TemplateRule.Discount.class));
        if (jsonObject.get("usage") != null)
            templateRule.setUsage(new Gson().fromJson(jsonObject.get("usage").getAsString(), TemplateRule.Usage.class));
        return coupon;
    }
}
