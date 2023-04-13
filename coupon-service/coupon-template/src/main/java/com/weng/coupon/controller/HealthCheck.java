package com.weng.coupon.controller;

import com.weng.coupon.exception.CouponException;
import com.weng.coupon.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HealthCheck {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private Registration registration;

    @GetMapping("/health")
    public CommonResponse health() {
        log.debug("view health api");
        CommonResponse response = new CommonResponse(0, "");
        response.setData("CouponTemplate Is OK!");
        return response;
    }

    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    @GetMapping("/info")
    public CommonResponse info() {
        // 大约需要等待两分钟时间才能获取到注册信息
        List<ServiceInstance> instances = discoveryClient.getInstances(registration.getServiceId());
        List<Map<String, Object>> res = new ArrayList<>();
        instances.forEach(e -> {
            Map<String, Object> infos = new HashMap<>();
            infos.put("serviceId", e.getServiceId());
            infos.put("instanceId", e.getInstanceId());
            infos.put("port", e.getPort());
            res.add(infos);
        });
        CommonResponse response = new CommonResponse(0, "");
        response.setData(res);
        return response;
    }
}
