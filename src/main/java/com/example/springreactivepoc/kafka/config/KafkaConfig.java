/*
 * Copyright (c) 2021. by 8x8. Inc.
 *  _____      _____
 * |  _  |    |  _  |
 *  \ V /__  __\ V /   ___ ___  _ __ ___
 *  / _ \\ \/ // _ \  / __/ _ \| '_ ` _ \
 * | |_| |>  <| |_| || (_| (_) | | | | | |
 * \_____/_/\_\_____(_)___\___/|_| |_| |_|
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of 8x8 Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with 8x8 Inc.
 */
package com.example.springreactivepoc.kafka.config;

import com.example.springreactivepoc.kafka.message.StatusChangeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import static java.util.Collections.singleton;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaReceiver<String, StatusChangeRequest> agentRequestsKafkaReceiver(KafkaProperties kafkaProperties,
                                                                                 @Value("${topics.agent-requests}") String agentRequestsTopic) {
        ReceiverOptions<String, StatusChangeRequest> options = ReceiverOptions.<String, StatusChangeRequest>create(kafkaProperties.buildConsumerProperties())
                .subscription(singleton(agentRequestsTopic));
        return KafkaReceiver.create(options);
    }


    @Bean
    public KafkaSender<String, Object> kafkaSender(KafkaProperties kafkaProperties) {
        SenderOptions<String, Object> options = SenderOptions.create(kafkaProperties.buildProducerProperties());
        return KafkaSender.create(options);
    }
}
