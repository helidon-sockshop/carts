package io.helidon.examples.sockshop.carts;

/**
 * Tests for default in memory repository implementation.
 */
class DefaultCartRepositoryTest extends CartRepositoryTest {
    @Override
    protected CartRepository getCartRepository() {
        return new DefaultCartRepository();
    }
}
