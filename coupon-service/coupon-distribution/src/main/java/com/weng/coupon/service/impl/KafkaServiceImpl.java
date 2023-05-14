package com.weng.coupon.service.impl;

import com.google.gson.Gson;
import com.weng.coupon.constant.Constant;
import com.weng.coupon.constant.CouponStatus;
import com.weng.coupon.dao.CouponMapper;
import com.weng.coupon.entity.Coupon;
import com.weng.coupon.service.KafkaService;
import com.weng.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {
    @Autowired
    private CouponMapper couponMapper;

    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional kafkaMessage = Optional.of(record.value());
        if (kafkaMessage.isPresent()) {
            CouponKafkaMessage couponKafkaMessage = new Gson().fromJson(kafkaMessage.get().toString(), CouponKafkaMessage.class);
            log.info("Receive CouponKafkaMessage: {}", couponKafkaMessage.toString());
            CouponStatus couponStatus = CouponStatus.of(couponKafkaMessage.getStatus());
            switch (couponStatus) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponKafkaMessage, couponStatus);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponKafkaMessage, couponStatus);
                    break;
            }
        }
    }

    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus couponStatus) {
        // TODO 给用户发送短信
        processCouponsByStatus(kafkaMessage, couponStatus);
    }

    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus couponStatus) {
        // TODO 给用户发送短信
        processCouponsByStatus(kafkaMessage, couponStatus);
    }

    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage, CouponStatus couponStatus){
        List<Coupon> coupons = couponMapper.findAllByIdsAndStatus(kafkaMessage.getIds(), couponStatus);
        if (coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can Not Find Right Coupon Info: {}",
                    new Gson().toJson(kafkaMessage));
            // TODO 发送邮件
            return;
        }
        coupons.forEach(e -> e.setStatus(couponStatus));
        log.info("CouponKafkaMessage Op Coupon Count: {}",
                couponMapper.saveAll(coupons));
    }
}
