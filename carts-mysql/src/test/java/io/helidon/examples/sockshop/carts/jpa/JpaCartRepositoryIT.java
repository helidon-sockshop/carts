package io.helidon.examples.sockshop.carts.jpa;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;
import io.helidon.microprofile.server.Server;

import org.junit.jupiter.api.AfterAll;

/**
 * Integration tests for {@link io.helidon.examples.sockshop.carts.jpa.JpaCartRepository}.
 */
public class JpaCartRepositoryIT extends CartRepositoryTest {

    /**
     * Starting server on ephemeral port in order to be able to get the
     * fully configured repository from the CDI container.
     */
    private static final Server SERVER = Server.builder().port(0).build().start();

    @AfterAll
    static void stopServer() {
        SERVER.stop();
    }

    @Override
    protected CartRepository getCartRepository() {
        return SERVER.cdiContainer().select(CartRepository.class).get();
    }
}
