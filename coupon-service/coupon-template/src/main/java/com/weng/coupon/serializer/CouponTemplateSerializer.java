package com.weng.coupon.serializer;

import com.google.gson.*;
import com.weng.coupon.entity.CouponTemplate;

import java.lang.reflect.Type;

public class CouponTemplateSerializer implements JsonSerializer<CouponTemplate> {
    @Override
    public JsonElement serialize(CouponTemplate couponTemplate, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject res = new JsonObject();
        res.addProperty("id", couponTemplate.getId());
        res.addProperty("available", couponTemplate.getAvailable());
        res.addProperty("expired", couponTemplate.getExpired());
        res.addProperty("name", couponTemplate.getName());
        res.addProperty("logo", couponTemplate.getLogo());
        res.addProperty("desc", couponTemplate.getDesc());
        res.addProperty("category", couponTemplate.getCategory().getDescription());
        res.addProperty("productLine", couponTemplate.getProductLine().getDescription());
        res.addProperty("count", couponTemplate.getCount());
        res.addProperty("createTime", couponTemplate.getCreateTime().toString("yyyy-MM-dd HH:mm:ss"));
        res.addProperty("userId", couponTemplate.getUserId());
        res.addProperty("key", couponTemplate.getKey() + String.format("%04d", couponTemplate.getId()));
        res.addProperty("target", couponTemplate.getTarget().getDescription());
        res.add("rule", new Gson().toJsonTree(couponTemplate.getRule()).getAsJsonObject());
        return res;
    }
}
