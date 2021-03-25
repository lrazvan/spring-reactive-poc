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
package com.example.springreactivepoc.kafka.consumer;

import com.example.springreactivepoc.kafka.handler.AgentStatusChangeHandler;
import com.example.springreactivepoc.kafka.message.StatusChangeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

@Component
@Slf4j
public class AgentRequestsConsumer implements ApplicationListener<ApplicationReadyEvent> {


    private final Scheduler scheduler = Schedulers.newBoundedElastic(8, 16, "agent-requests");
    private final KafkaReceiver<String, StatusChangeRequest> agentRequestsKafkaReceiver;
    private final AgentStatusChangeHandler agentStatusChangeHandler;


    public AgentRequestsConsumer(KafkaReceiver<String, StatusChangeRequest> agentRequestsKafkaReceiver,
                                 AgentStatusChangeHandler agentStatusChangeHandler) {
        this.agentRequestsKafkaReceiver = agentRequestsKafkaReceiver;
        this.agentStatusChangeHandler = agentStatusChangeHandler;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        agentRequestsKafkaReceiver.receive()
                .groupBy(m -> m.receiverOffset().topicPartition())
                .flatMap(partitionFlux -> partitionFlux.publishOn(scheduler)
                        .map(this::processRecord)
                        //.sample(Duration.ofMillis(5000))
                        .concatMap(ReceiverOffset::commit))
                .subscribe();
    }

    private ReceiverOffset processRecord(ReceiverRecord<String, StatusChangeRequest> record) {
        log.info("Received request with key=[{}], value=[{}]", record.key(), record.value());
        agentStatusChangeHandler.handle(record.value());
        return record.receiverOffset();
    }
}