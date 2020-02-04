package io.helidon.examples.sockshop.carts.redis;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.DefaultCartRepository;

import org.eclipse.microprofile.opentracing.Traced;
import org.redisson.api.RMap;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses Redis (via Redisson) as a backend data store.
 */
@ApplicationScoped
@Specializes
@Traced
public class RedisCartRepository extends DefaultCartRepository {
    @Inject
    public RedisCartRepository(RMap<String, Cart> carts) {
        super(carts);
    }
}
