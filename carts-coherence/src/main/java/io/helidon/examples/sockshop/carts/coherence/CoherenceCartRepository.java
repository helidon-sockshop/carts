/*
 *  Copyright (c) 2020 Oracle and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.helidon.examples.sockshop.carts.coherence;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.DefaultCartRepository;

import io.helidon.examples.sockshop.carts.Item;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import javax.inject.Inject;

import com.oracle.coherence.cdi.Cache;
import com.tangosol.net.NamedCache;
import org.eclipse.microprofile.opentracing.Traced;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class CoherenceCartRepository extends DefaultCartRepository {
    protected final NamedCache<String, Cart> carts;

    @Inject
    CoherenceCartRepository(@Cache("carts") NamedCache<String, Cart> carts) {
        super(carts);
        this.carts = carts;
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }

    @Override
    public void deleteCart(String customerId) {
        carts.remove(customerId);
    }

    @Override
    public Item addItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public Item updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        });
    }
}
