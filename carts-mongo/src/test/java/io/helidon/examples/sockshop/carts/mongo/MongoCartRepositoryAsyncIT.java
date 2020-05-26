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

package io.helidon.examples.sockshop.carts.mongo;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;
import io.helidon.examples.sockshop.carts.SyncCartRepository;

import static io.helidon.examples.sockshop.carts.mongo.MongoProducers.asyncCarts;
import static io.helidon.examples.sockshop.carts.mongo.MongoProducers.asyncClient;
import static io.helidon.examples.sockshop.carts.mongo.MongoProducers.asyncDb;

/**
 * Integration tests for {@link MongoCartRepositoryAsync}.
 */
class MongoCartRepositoryAsyncIT extends CartRepositoryTest {
    public CartRepository getCartRepository() {
        String host = System.getProperty("db.host","localhost");
        int    port = Integer.parseInt(System.getProperty("db.port","27017"));

        return new SyncCartRepository(new MongoCartRepositoryAsync(asyncCarts(asyncDb(asyncClient(host, port)))));
    }
}
