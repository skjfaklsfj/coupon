package com.weng.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaService {
    /**
     * <h2>消费优惠券 Kafka 消息</h2>
     * @param record {@link ConsumerRecord}
     * */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
