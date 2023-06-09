package com.weng.coupon.executor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weng.coupon.vo.GoodsInfo;
import com.weng.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>规则执行器抽象类, 定义通用方法</h1>
 */
public abstract class AbstractExecutor {
    /**
     * <h2>校验商品类型与优惠券是否匹配</h2>
     * 需要注意:
     * 1. 这里实现的单品类优惠券的校验, 多品类优惠券重载此方法
     * 2. 商品只需要有一个优惠券要求的商品类型去匹配就可以
     * */
    protected Boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        List<Integer> goodsType = settlementInfo.getGoodsInfos().stream().map(GoodsInfo::getType).collect(Collectors.toList());
        Type type = new TypeToken<List<Integer>>(){}.getType();
        List<Integer> templateGoodsType = new Gson().fromJson(settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate().getRule().getUsage().getGoodsType(), type);
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * <h2>处理商品类型与优惠券限制不匹配的情况</h2>
     * @param settlementInfo {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum 商品总价
     * @return {@link SettlementInfo} 已经修改过的结算信息
     * */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo, Double goodsSum) {
        if (!isGoodsTypeSatisfy(settlementInfo)) {
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        } else {
            return null;
        }
    }

    /**
     * <h2>商品总价</h2>
     * */
    protected Double goodsSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(goodsInfo -> goodsInfo.getPrice() * goodsInfo.getCount()).sum();
    }

    /**
     * <h2>保留两位小数</h2>
     * */
    protected double retain2Decimals(double value) {

        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * <h2>最小支付费用</h2>
     * */
    protected double minCost() {

        return 0.1;
    }


}
