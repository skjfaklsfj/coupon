package com.weng.coupon.schedule;

import com.weng.coupon.dao.CouponTemplateMapper;
import com.weng.coupon.entity.CouponTemplate;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ScheduledTask {

    @Autowired
    private CouponTemplateMapper couponTemplateMapper;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {
        log.info("Start to expire couponTemplate");
        List<CouponTemplate> templateList = couponTemplateMapper.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templateList)) {
            log.info("Done To Expire CouponTemplate.");
            return;
        }
        DateTime now = new DateTime();
        List<CouponTemplate> expiredTemplates = new ArrayList<>();
        templateList.forEach(e -> {
            if (e.getRule().getExpiration().getDeadline() < now.getMillis()) {
                e.setExpired(true);
                expiredTemplates.add(e);
            }
        });
        if (!CollectionUtils.isEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num: {}", couponTemplateMapper.updateCouponTemplates(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate.");
    }
}
