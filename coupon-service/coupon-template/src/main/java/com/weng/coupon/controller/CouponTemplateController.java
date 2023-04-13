package com.weng.coupon.controller;

import com.google.gson.Gson;
import com.weng.coupon.entity.CouponTemplate;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.service.BuildTemplateService;
import com.weng.coupon.service.TemplateBaseService;
import com.weng.coupon.vo.CommonResponse;
import com.weng.coupon.vo.CouponTemplateSDK;
import com.weng.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CouponTemplateController {
    @Autowired
    private BuildTemplateService buildTemplateService;
    @Autowired
    private TemplateBaseService templateBaseService;

    /**
     * <h2>构建优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/imooc/coupon-template/template/build
     * */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("build template:{}", new Gson().toJson(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * <h2>构造优惠券模板详情</h2>
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     * 127.0.0.1:9000/imooc/coupon-template/template/info?id=1
     * */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id")Integer id) throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * 127.0.0.1:9000/imooc/coupon-template/template/sdk/all
     * */
    @GetMapping("/template/sdk/all")
    public CommonResponse findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        CommonResponse response = new CommonResponse(0, "");
        response.setData(templateBaseService.findAllUsableTemplate());
        return response;
    }

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     * 127.0.0.1:9000/imooc/coupon-template/template/sdk/infos?ids=1,2
     * */
    @GetMapping("/template/sdk/infos")
    public CommonResponse findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids) {
        log.info("FindIds2TemplateSDK: {}", new Gson().toJson(ids));
        CommonResponse response = new CommonResponse(0, "");
        response.setData(templateBaseService.findIds2TemplateSDK(ids));
        return response;
    }

}
