package com.weng.coupon;

import com.google.gson.Gson;
import com.weng.coupon.constant.CouponCategory;
import com.weng.coupon.constant.GoodsType;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.executor.ExecutorManager;
import com.weng.coupon.vo.CouponTemplateSDK;
import com.weng.coupon.vo.GoodsInfo;
import com.weng.coupon.vo.SettlementInfo;
import com.weng.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ExecutorTest {
    /** fake 一个 UserId */
    private Long fakeUserId = 20001L;

    @Autowired
    private ExecutorManager manager;

    @Test
    public void testComputeRule() throws CouponException {

//         满减优惠券结算测试
//        log.info("ManJian Coupon Executor Test");
//        SettlementInfo manjianInfo = fakeManJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(manjianInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 折扣优惠券结算测试
//        log.info("ZheKou Coupon Executor Test");
//        SettlementInfo zhekouInfo = fakeZheKouCouponSettlement();
//        SettlementInfo result = manager.computeRule(zhekouInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 立减优惠券结算测试
//        log.info("LiJian Coupon Executor Test");
//        SettlementInfo lijianInfo = fakeLiJianCouponSettlement();
//
//        SettlementInfo result = manager.computeRule(lijianInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 满减折扣优惠券结算测试
        log.info("ManJian ZheKou Coupon Executor Test");
        SettlementInfo manjianZheKouInfo = fakeManJianAndZheKouCouponSettlement();

        SettlementInfo result = manager.computeRule(manjianZheKouInfo);

        log.info("{}", result.getCost());
        log.info("{}", result.getCouponAndTemplateInfos().size());
        log.info("{}", result.getCouponAndTemplateInfos());
    }

    /**
     * <h2>fake(mock) 满减优惠券的结算信息</h2>
     * */
    private SettlementInfo fakeManJianCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        // 达到满减标准
        goodsInfo02.setCount(10);
        // 没有达到满减标准
//        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        templateSDK.setKey("100120190801");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20, 199));
        rule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                new Gson().toJson(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        templateSDK.setRule(rule);

        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 折扣优惠券的结算信息</h2>
     * */
    private SettlementInfo fakeZheKouCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK.setKey("100220190712");

        // 设置 TemplateRule
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(85, 1));
//        rule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
//                new Gson().toJson(Arrays.asList(
//                        GoodsType.WENYU.getCode(),
//                        GoodsType.JIAJU.getCode()
//                ))));
        rule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                new Gson().toJson(Arrays.asList(
                        GoodsType.SHENGXIAN.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 立减优惠券的结算信息</h2>
     * */
    private SettlementInfo fakeLiJianCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(3);
        templateSDK.setCategory(CouponCategory.LIJIAN.getCode());
        templateSDK.setKey("200320190712");

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                new Gson().toJson(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 满减 + 折扣优惠券结算信息</h2>
     * */
    private SettlementInfo fakeManJianAndZheKouCouponSettlement() {

        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        // 满减优惠券
        SettlementInfo.CouponAndTemplateInfo manjianInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        manjianInfo.setId(1);

        CouponTemplateSDK manjianTemplate = new CouponTemplateSDK();
        manjianTemplate.setId(1);
        manjianTemplate.setCategory(CouponCategory.MANJIAN.getCode());
        manjianTemplate.setKey("100120190712");

        TemplateRule manjianRule = new TemplateRule();
        manjianRule.setDiscount(new TemplateRule.Discount(20, 199));
        manjianRule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                new Gson().toJson(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        manjianRule.setWeight(new Gson().toJson(Collections.emptyList()));
        manjianTemplate.setRule(manjianRule);
        manjianInfo.setTemplate(manjianTemplate);

        // 折扣优惠券
        SettlementInfo.CouponAndTemplateInfo zhekouInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        zhekouInfo.setId(1);

        CouponTemplateSDK zhekouTemplate = new CouponTemplateSDK();
        zhekouTemplate.setId(2);
        zhekouTemplate.setCategory(CouponCategory.ZHEKOU.getCode());
        zhekouTemplate.setKey("100220190712");

        TemplateRule zhekouRule = new TemplateRule();
        zhekouRule.setDiscount(new TemplateRule.Discount(85, 1));
        zhekouRule.setUsage(new TemplateRule.Usage("安徽省", "桐城市",
                new Gson().toJson(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()
                ))));
        zhekouRule.setWeight(new Gson().toJson(
                Collections.singletonList("1001201907120001")
        ));
        zhekouTemplate.setRule(zhekouRule);
        zhekouInfo.setTemplate(zhekouTemplate);

        info.setCouponAndTemplateInfos(Arrays.asList(
                manjianInfo, zhekouInfo
        ));

        return info;
    }
}
