package com.weng.coupon.executor.impl;

import com.weng.coupon.constant.RuleFlag;
import com.weng.coupon.executor.AbstractExecutor;
import com.weng.coupon.executor.RuleExecutor;
import com.weng.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        Double goodsSum = retain2Decimals(goodsSum(settlement.getGoodsInfos()));
        SettlementInfo settlementInfo = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (settlementInfo != null) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return settlementInfo;
        }
        Double quota = Double.valueOf(settlement.getCouponAndTemplateInfos().get(0).getTemplate().getRule().getDiscount().getQuota());
        settlement.setCost(retain2Decimals(goodsSum - quota) > minCost() ?
                retain2Decimals(goodsSum - quota) : minCost());
        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());

        return settlement;
    }
}
