package io.helidon.examples.sockshop.carts.jpa;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;

import static io.helidon.examples.sockshop.carts.CartResourceIT.SERVER;

/**
 * Integration tests for {@link io.helidon.examples.sockshop.carts.jpa.JpaCartRepository}.
 */
public class JpaCartRepositoryIT extends CartRepositoryTest {
    @Override
    protected CartRepository getCartRepository() {
        return SERVER.cdiContainer().select(CartRepository.class).get();
    }
}
