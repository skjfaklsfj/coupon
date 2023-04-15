package com.weng.coupon;

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
        List<Coupon> list = mapper.findAllByUserIdAndStatus(3L, CouponStatus.USABLE);
        list.forEach(System.out::println);
    }
}
