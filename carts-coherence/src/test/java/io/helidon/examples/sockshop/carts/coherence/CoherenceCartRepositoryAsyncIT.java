/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.coherence;

import com.tangosol.net.CacheFactory;

import com.tangosol.net.NamedMap;
import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;
import io.helidon.examples.sockshop.carts.SyncCartRepository;

import static com.tangosol.net.cache.TypeAssertion.withTypes;

/**
 * Tests for Coherence repository implementation.
 */
class CoherenceCartRepositoryAsyncIT extends CartRepositoryTest {
    @Override
    protected CartRepository getCartRepository() {
        NamedMap<String, Cart> carts = CacheFactory.getCache("carts", withTypes(String.class, Cart.class));
        return new SyncCartRepository(new CoherenceCartRepositoryAsync(carts.async()));
    }
}
