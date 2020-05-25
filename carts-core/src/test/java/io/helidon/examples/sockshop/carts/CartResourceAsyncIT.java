package io.helidon.examples.sockshop.carts;

/**
 * Integration tests for {@link io.helidon.examples.sockshop.carts.CartResourceAsync}.
 */
public class CartResourceAsyncIT extends CartResourceIT {
    protected String getBasePath() {
        return "/carts-async";
    }

    protected CartRepository getCartsRepository() {
        return new SyncCartRepository(SERVER.cdiContainer().select(CartRepositoryAsync.class).get());
    }
}
