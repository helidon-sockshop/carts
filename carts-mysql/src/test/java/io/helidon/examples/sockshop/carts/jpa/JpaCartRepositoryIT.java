package io.helidon.examples.sockshop.carts.jpa;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;

/**
 * @author Aleksandar Seovic  2020.01.27
 */
public class JpaCartRepositoryIT extends CartRepositoryTest {

    @Override
    protected CartRepository getCartRepository() {
        SeContainer cdi = SeContainerInitializer.newInstance().initialize();
        return cdi.select(CartRepository.class).get();
    }
}
