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
package com.example.springreactivepoc.kafka.handler;

import com.example.springreactivepoc.kafka.EventSender;
import com.example.springreactivepoc.kafka.message.StatusChangeRequest;
import com.example.springreactivepoc.kafka.message.StatusChangedEvent;
import com.example.springreactivepoc.redis.model.Agent;
import com.example.springreactivepoc.redis.repository.AgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AgentStatusChangeHandler {

    private final AgentRepository agentRepository;
    private final EventSender eventSender;

    public AgentStatusChangeHandler(AgentRepository agentRepository,
                                    EventSender eventSender) {
        this.agentRepository = agentRepository;
        this.eventSender = eventSender;
    }

    public void handle(StatusChangeRequest request) {
        agentRepository.get(request.getId())
                .doOnError(e -> log.error("Could not found agent for request=[{}]", request, e))
                .switchIfEmpty(Mono.error(new RuntimeException("Could not find agent")))
                .doOnSuccess(agent -> log.info("Found agent=[{}]", agent))
                .flatMap(agent -> updateStatus(request, agent))
                .flatMap(this::sendEvents)
                .subscribe();
    }

    private Mono<Agent> updateStatus(StatusChangeRequest request, Agent agent) {
        Agent updated = agent.toBuilder()
                .status(request.getStatus())
                .build();
        return agentRepository.save(updated)
                .then(Mono.just(updated));
    }

    private Mono<Void> sendEvents(Agent agent) {
        StatusChangedEvent event = StatusChangedEvent.builder()
                .id(agent.getId())
                .status(agent.getStatus())
                .build();
        return eventSender.sendEvent(event);
    }

}
