package io.helidon.examples.sockshop.carts.mongo;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;

import static io.helidon.examples.sockshop.carts.mongo.MongoProducers.*;

/**
 * Tests for Mongo repository implementation.
 */
class MongoCartRepositoryIT extends CartRepositoryTest {
    public CartRepository getCartRepository() {
        String host = System.getProperty("db.host","localhost");
        int    port = Integer.parseInt(System.getProperty("db.port","27017"));

        return new MongoCartRepository(carts(db(client(host, port))));
    }
}
