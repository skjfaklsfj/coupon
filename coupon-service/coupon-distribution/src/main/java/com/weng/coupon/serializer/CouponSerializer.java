package com.weng.coupon.serializer;

import com.google.gson.*;
import com.weng.coupon.entity.Coupon;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class CouponSerializer implements JsonSerializer<Coupon> {
    @Override
    public JsonElement serialize(Coupon coupon, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject res = new JsonObject();
        res.addProperty("id", coupon.getId());
        res.addProperty("templateId", coupon.getTemplateId());
        res.addProperty("userId", coupon.getUserId());
        res.addProperty("couponCode", coupon.getCouponCode());
        res.addProperty("assignTime", coupon.getAssignTime().toString("yyyy-MM-dd HH:mm:ss"));
        res.addProperty("name", coupon.getTemplateSDK().getName());
        res.addProperty("logo", coupon.getTemplateSDK().getLogo());
        res.addProperty("desc", coupon.getTemplateSDK().getDesc());
        res.addProperty("expiration", new Gson().toJson(coupon.getTemplateSDK().getRule().getExpiration()));
        res.addProperty("discount", new Gson().toJson(coupon.getTemplateSDK().getRule().getDiscount()));
        res.addProperty("usage", new Gson().toJson(coupon.getTemplateSDK().getRule().getUsage()));
        return res;
    }
}
