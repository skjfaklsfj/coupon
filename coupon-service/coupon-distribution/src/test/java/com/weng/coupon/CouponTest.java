package com.weng.coupon;

import com.google.gson.Gson;
import com.weng.coupon.constants.CouponStatus;
import com.weng.coupon.dao.CouponMapper;
import com.weng.coupon.entity.Coupon;
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
    @Test
    public void testCoupon() {
        System.out.println(new Gson().toJson(CouponStatus.USABLE));
    }
}
