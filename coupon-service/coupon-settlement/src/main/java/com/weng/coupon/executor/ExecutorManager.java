package com.weng.coupon.executor;

import com.weng.coupon.constant.CouponCategory;
import com.weng.coupon.constant.RuleFlag;
import com.weng.coupon.exception.CouponException;
import com.weng.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.weng.coupon.constant.RuleFlag.*;

@Slf4j
@Component
public class ExecutorManager implements BeanPostProcessor {
    private Map<RuleFlag, RuleExecutor> flag2Executor = new HashMap<>();
    /**
     * <h2>优惠券结算规则计算入口</h2>
     * 注意: 一定要保证传递进来的优惠券个数 >= 1
     * */
    public SettlementInfo computeRule(SettlementInfo settlement)
            throws CouponException {
        SettlementInfo result = null;
        if (settlement.getCouponAndTemplateInfos().size() == 1) {
            switch (CouponCategory.of(settlement.getCouponAndTemplateInfos().get(0).getTemplate().getCategory())) {
                case MANJIAN:
                    result = flag2Executor.get(MANJIAN).computeRule(settlement);
                    break;
                case LIJIAN:
                    result = flag2Executor.get(LIJIAN).computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = flag2Executor.get(ZHEKOU).computeRule(settlement);
                    break;
            }
        } else {
            if (settlement.getCouponAndTemplateInfos().size() >= 3) {
                throw new CouponException("Not Support For Other " +
                        "Template Category");
            }
            List<CouponCategory> categories = settlement.getCouponAndTemplateInfos().stream().map(info -> CouponCategory.of(info.getTemplate().getCategory())).collect(Collectors.toList());
            if (categories.contains(CouponCategory.MANJIAN) && categories.contains(CouponCategory.ZHEKOU)) {
                result = flag2Executor.get(MANJIAN_ZHEKOU).computeRule(settlement);
            } else {
                throw new CouponException("Not Support For Other " +
                        "Template Category");
            }
        }
        return result;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RuleExecutor) {
            RuleExecutor executor = (RuleExecutor) bean;
            RuleFlag ruleFlag = executor.ruleConfig();
            if (flag2Executor.containsKey(ruleFlag)) {
                throw new IllegalStateException("There is already an executor" +
                        "for rule flag: " + ruleFlag);
            }
            log.info("Load executor {} for rule flag {}.",
                    executor.getClass(), ruleFlag);
            flag2Executor.put(ruleFlag, executor);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
