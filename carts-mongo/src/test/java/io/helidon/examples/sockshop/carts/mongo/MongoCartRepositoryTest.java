package io.helidon.examples.sockshop.carts.mongo;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;

import static io.helidon.examples.sockshop.carts.mongo.MongoProducers.*;

/**
 * Tests for Mongo repository implementation.
 */
class MongoCartRepositoryTest extends CartRepositoryTest {
    public CartRepository getCartRepository() {
        return new MongoCartRepository(carts(db(client("localhost"))));
    }
}
