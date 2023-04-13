package com.weng.coupon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.weng.coupon.constant.CouponCategory;
import com.weng.coupon.dao.CouponTemplateMapper;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.service.AsyncService;
import com.weng.coupon.vo.TemplateRule;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CouponTemplateTest {
    @Autowired
    private CouponTemplateMapper mapper;
    @Autowired
    private AsyncService asyncService;

    @Autowired
    private StringRedisTemplate template;
    @Test
    public void testGson() throws InterruptedException {
//        {
//            "expiration": {
//                "period": 1,
//                "gap": 5,
//                "deadline": 1000
//            },
//            "discount": {
//                "quota": 50,
//                "base": 1000
//            },
//            "limitation": 1000,
//            "usage": {
//                "province": "浙江",
//                "city": "衢州",
//                "goodstype": "[生鲜]"
//            },
//            "weight": "001"
//        }
//        Date date = new Date();
//        Gson gson = new Gson();
//        Gson gson1 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//        String s = gson1.toJson(date);
//        Date date1 = gson1.fromJson(s, Date.class);
//        System.out.println(date1);

//        TemplateRule.Expiration expiration = new TemplateRule.Expiration(5, 5, 4L);
//        TemplateRule.Usage usage = new TemplateRule.Usage("浙江", "杭州", "生鲜");
//        TemplateRule.Discount discount = new TemplateRule.Discount(50, 400);
//        TemplateRule templateRule = new TemplateRule(expiration, discount, 100, usage, "100");
//        CouponTemplate couponTemplate = new CouponTemplate("abc", "dcf", "你好", "001", 1, 1, 100L, 1, templateRule);
//        couponTemplate.setId(12);
        List<CouponTemplate> list = new ArrayList<>();
        CouponTemplate couponTemplate = mapper.findByName("abcd");
        couponTemplate.setName("abc");
        list.add(couponTemplate);
        couponTemplate = mapper.findByName("sdd");
        couponTemplate.setName("sd");
        list.add(couponTemplate);
        mapper.updateCouponTemplates(list);
//        DateTime dateTime = new DateTime();
//        JsonObject jsonObject = new Gson().toJsonTree(dateTime).getAsJsonObject();

    }
}
