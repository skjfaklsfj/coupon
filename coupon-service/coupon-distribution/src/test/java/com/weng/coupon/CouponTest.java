package com.weng.coupon;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.weng.coupon.constant.CouponStatus;
import com.weng.coupon.dao.CouponMapper;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.service.UserService;
import com.weng.coupon.vo.CouponTemplateSDK;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponTest {
    @Autowired
    private CouponMapper mapper;
    @Autowired
    private UserService userService;
    @Test
    public void testCoupon() throws CouponException {
        System.out.println(new Gson().toJson(userService.findCouponsByStatus(2111L, CouponStatus.USABLE.getCode())));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {
        System.out.println(new Gson().toJson(userService.findAvailableTemplate(1L)));
    }


}
