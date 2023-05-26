package com.weng.coupon.controller;

import com.google.gson.Gson;
import com.weng.coupon.annotation.IgnoreResponseAdvice;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.executor.ExecutorManager;
import com.weng.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SettlementController {
    @Autowired
    private ExecutorManager executorManager;
    /**
     * <h2>优惠券结算</h2>
     * 127.0.0.1:7003/coupon-settlement/settlement/compute
     * 127.0.0.1:9000/imooc/coupon-settlement/settlement/compute
     * */
    @PostMapping("/settlement/compute")
    @IgnoreResponseAdvice
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement)
            throws CouponException {
        log.info("settlement: {}", new Gson().toJson(settlement));
        return executorManager.computeRule(settlement);
    }
}
