package com.weng.coupon;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.weng.coupon.constant.CouponStatus;
import com.weng.coupon.dao.CouponMapper;
import com.weng.coupon.entity.Coupon;
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
    @Test
    public void testCoupon() {
        List<Coupon> coupons = mapper.findAllByIdsAndStatus(Lists.newArrayList(10, 11), CouponStatus.USABLE);
        coupons.forEach(e -> e.setStatus(CouponStatus.EXPIRED));
        System.out.println(mapper.saveAll(coupons));
    }


}
