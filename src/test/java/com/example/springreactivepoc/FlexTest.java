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
package com.example.springreactivepoc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * WHAT IS MY PURPOSE?
 *
 * @author rlupu2
 */
@Slf4j
public class FlexTest {

    @Test
    public void test() {

        Scheduler scheduler = Schedulers.elastic();

        Flux.range(1, 5)
                .map(n -> Mono.just(n).publishOn(scheduler).map(k -> process(k))
                        .thenEmpty(m -> log.info("Job Done")).subscribe()).subscribe();
    }

    public Mono<Object> process(Object n) {
        log.info("Processing : {}", n);
        return Mono.empty();
    }
}