package com.weng.coupon.feign;

import com.weng.coupon.exception.CouponException;
import com.weng.coupon.feign.hystrix.SettlementClientHystrix;
import com.weng.coupon.vo.CommonResponse;
import com.weng.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {
    /**
     * <h2>优惠券规则计算</h2>
     * */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",
            method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(
            @RequestBody SettlementInfo settlement) throws CouponException;
}
