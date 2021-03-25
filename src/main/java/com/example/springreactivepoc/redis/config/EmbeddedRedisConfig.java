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
package com.example.springreactivepoc.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;
import redis.embedded.exceptions.EmbeddedRedisException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@Slf4j
public class EmbeddedRedisConfig {
    private static final int PORT = 6379;
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        log.info("Starting embedded redis at port {}", PORT);
        try {
            redisServer = new RedisServer(PORT);
            redisServer.start();
            log.info("Embedded redis server started at port {}", PORT);
        } catch (Exception e) {
            log.error("Exception starting embedded redis server at port {}", PORT);
        }
    }

    @PreDestroy
    public void stopRedis() {
        int stopCount = 0;
        while (redisServer.isActive() && stopCount < 3) {
            try {
                redisServer.stop();
            } catch (EmbeddedRedisException e) {
                log.error("Failed to stop embedded Redis instance on port {}", PORT);
            }
            stopCount++;
        }
        if (stopCount == 3) {
            log.error("Embedded Redis instance needs to be stopped manually.");
        }
    }
}
