package com.weng.coupon.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.lang.reflect.Modifier;

@Configuration
public class GsonConfig {
    @Bean
        //自己提供一个GsonHttpMessageConverter的实例
    GsonHttpMessageConverter gsonHttpMessageConverter(){
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        GsonBuilder builder = new GsonBuilder();
        //设置Gson解析时日期的格式
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置Gson解析时修饰符为protected的字段被过滤掉
        builder.excludeFieldsWithModifiers(Modifier.PROTECTED);
        //设置序列化
        //创建Gson对象放入GsonHttpMessageConverter的实例中并返回converter
        Gson gson = builder.create();
        converter.setGson(gson);
        return converter;
    }
}
