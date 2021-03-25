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
package com.example.springreactivepoc.redis.repository;


import com.example.springreactivepoc.redis.model.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class AgentRepository {

    private static final String AGENTS_HASH = "agents";

    private final ReactiveHashOperations<String, String, Agent> agentOperations;

    public AgentRepository(ReactiveHashOperations<String, String, Agent> agentOperations) {
        this.agentOperations = agentOperations;
    }

    public Flux<Agent> getAll() {
        log.info("Getting all agents");
        return agentOperations.values(AGENTS_HASH);
    }

    public Mono<Agent> get(String id) {
        log.info("Getting agent with id=[{}]", id);
        return agentOperations.get(AGENTS_HASH, id);
    }

    public Mono<Agent> save(Agent agent) {
        log.info("Saving agent=[{}]", agent);
        return agentOperations.put(AGENTS_HASH, agent.getId(), agent)
                .then(Mono.just(agent));
    }

}
