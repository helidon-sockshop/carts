/*
 *  Copyright (c) 2020 Oracle and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.helidon.examples.sockshop.carts.redis;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.helidon.config.Config;
import io.helidon.examples.sockshop.carts.Cart;

import lombok.extern.java.Log;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import static java.lang.String.format;

/**
 * CDI support for Redis/Redisson.
 */
@ApplicationScoped
@Log
public class RedisProducers {

    /**
     * Default Redis host to connect to.
     */
    public static final String DEFAULT_HOST = "carts-db";

    /**
     * Default Redis port to connect to.
     */
    public static final int DEFAULT_PORT = 6379;

    /**
     * CDI Producer for {@code RedissonClient}.
     *
     * @param config application configuration, which will be used to read
     *               the values of a {@code db.host} and {@code db.port}
     *               configuration properties, if present. If either is not
     *               present, the defaults defined by the {@link #DEFAULT_HOST}
     *               and {@link #DEFAULT_PORT} constants will be used.
     *
     * @return a {@code RedissonClient} instance
     */
    @Produces
    @ApplicationScoped
    public static RedissonClient client(Config config) {
        Config db = config.get("db");
        return client(db.get("host").asString().orElse(DEFAULT_HOST),
                      db.get("port").asInt().orElse(DEFAULT_PORT));
    }

    /**
     * CDI Producer for the {@code RMap} that contains
     * shopping carts.
     *
     * @param client the {@code RedissonClient} to use
     *
     * @return a {@code RMap} instance for the shopping carts
     */
    @Produces
    @ApplicationScoped
    public static RMap<String, Cart> carts(RedissonClient client) {
        return client.getMap("carts");
    }

    // ---- helpers ---------------------------------------------------------

    /**
     * Create {@code RedissonClient} for the specified host and port.
     *
     * @param host the Redis host to connect to
     * @param port the Redis port to connect to
     *
     * @return a {@code RedissonClient} instance
     */
    static RedissonClient client(String host, int port) {
        log.info(format("Connecting to Redis on host %s:%d", host, port));

        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer()
                .setAddress(format("redis://%s:%d", host, port));

        return Redisson.create(config);
    }

}
