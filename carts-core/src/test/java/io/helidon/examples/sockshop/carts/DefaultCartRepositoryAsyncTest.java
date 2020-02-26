package io.helidon.examples.sockshop.carts;

/**
 * Tests for default in memory repository implementation.
 */
class DefaultCartRepositoryAsyncTest extends CartRepositoryTest {
    @Override
    protected CartRepository getCartRepository() {
        return new SyncCartRepository(new DefaultCartRepositoryAsync());
    }

}
