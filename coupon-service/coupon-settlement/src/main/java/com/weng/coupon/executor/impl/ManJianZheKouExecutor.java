package com.weng.coupon.executor.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weng.coupon.constant.CouponCategory;
import com.weng.coupon.constant.RuleFlag;
import com.weng.coupon.executor.AbstractExecutor;
import com.weng.coupon.executor.RuleExecutor;
import com.weng.coupon.vo.GoodsInfo;
import com.weng.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * <h2>校验商品类型与优惠券是否匹配</h2>
     * 需要注意:
     * 1. 这里实现的满减 + 折扣优惠券的校验
     * 2. 如果想要使用多类优惠券, 则必须要所有的商品类型都包含在内, 即差集为空
     * @param settlementInfo {@link SettlementInfo} 用户传递的计算信息
     */
    @Override
    protected Boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        Set<Integer> goodsType = settlementInfo.getGoodsInfos().stream().map(GoodsInfo::getType).collect(Collectors.toSet());
        Set<Integer> templateGoodsType = new HashSet<>();
        Type type = new TypeToken<List<Integer>>(){}.getType();
        settlementInfo.getCouponAndTemplateInfos().forEach(i -> {
            templateGoodsType.addAll(new Gson().fromJson(i.getTemplate().getRule().getUsage().getGoodsType(), type));
        });
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType, templateGoodsType));
    }

    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        Double goodsSum = goodsSum(settlement.getGoodsInfos());
        SettlementInfo settlementInfo = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (settlementInfo != null) {
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return settlementInfo;
        }
        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;
        for (SettlementInfo.CouponAndTemplateInfo info: settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(info.getTemplate().getCategory()) == CouponCategory.MANJIAN) {
                manJian = info;
            } else {
                zheKou = info;
            }
        }

        if (!isTemplateCanShare(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        Double manJianBase = Double.valueOf(manJian.getTemplate().getRule().getDiscount().getBase());
        Double manJianQuota = Double.valueOf(manJian.getTemplate().getRule().getDiscount().getQuota());
        List<SettlementInfo.CouponAndTemplateInfo> infos = new ArrayList<>();
        Double targetSum = goodsSum;
        if (targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            infos.add(manJian);
        }
        Double zheKouQuota = Double.valueOf(zheKou.getTemplate().getRule().getDiscount().getQuota());
        targetSum *= zheKouQuota * 1.0 / 100;
        infos.add(zheKou);
        settlement.setCost(retain2Decimals(targetSum > minCost() ? targetSum: minCost()));
        settlement.setCouponAndTemplateInfos(infos);
        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());
        return settlement;
    }

    private Boolean isTemplateCanShare(SettlementInfo.CouponAndTemplateInfo manJian, SettlementInfo.CouponAndTemplateInfo zheKou) {
        String manJianKey = manJian.getTemplate().getKey() + String.format("%04d", manJian.getTemplate().getId());
        String zheKouKey = zheKou.getTemplate().getKey() + String.format("%04d", zheKou.getTemplate().getId());
        List<String> manJianShareList = new Gson().fromJson(manJian.getTemplate().getRule().getWeight(), List.class);
        List<String> zheKouShareList = new Gson().fromJson(zheKou.getTemplate().getRule().getWeight(), List.class);
        return manJianShareList.contains(zheKouKey) || zheKouShareList.contains(manJianKey);
    }
}
