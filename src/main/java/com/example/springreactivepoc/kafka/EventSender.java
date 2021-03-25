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
package com.example.springreactivepoc.kafka;

import com.example.springreactivepoc.kafka.message.StatusChangeRequest;
import com.example.springreactivepoc.kafka.message.StatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@Slf4j
public class EventSender {
    private final KafkaSender<String, Object> kafkaSender;
    private final String agentRequestsTopic;
    private final String agentEventsTopic;

    public EventSender(KafkaSender<String, Object> kafkaSender,
                       @Value("${topics.agent-requests}") String agentRequestsTopic,
                       @Value("${topics.agent-events}") String agentEventsTopic) {
        this.kafkaSender = kafkaSender;
        this.agentRequestsTopic = agentRequestsTopic;
        this.agentEventsTopic = agentEventsTopic;
    }

    public Mono<Void> sendRequest(StatusChangeRequest request) {
        return doSend(new ProducerRecord<>(agentRequestsTopic, request.getId(), request));
    }

    public Mono<Void> sendEvent(StatusChangedEvent event) {
        return doSend(new ProducerRecord<>(agentEventsTopic, event.getId(), event));
    }

    private Mono<Void> doSend(ProducerRecord<String, Object> record) {
        log.info("Sending event=[{}] on topic=[{}]", record.value(), record.topic());
        SenderRecord<String, Object, String> senderRecord = SenderRecord.create(record, null);
        Mono<SenderRecord<String, Object, String>> mono = Mono.just(senderRecord);
        return kafkaSender.send(mono).then();
    }
}
