/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.coherence;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.DefaultCartRepository;
import io.helidon.examples.sockshop.carts.Item;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.oracle.coherence.cdi.Name;

import com.tangosol.net.NamedMap;

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
    protected final NamedMap<String, Cart> carts;

    @Inject
    CoherenceCartRepository(@Name("carts") NamedMap<String, Cart> carts) {
        super(carts);
        this.carts = carts;
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        final Cart source = carts.remove(sourceId);
        if (source == null) {
            return false;
        }
        
        carts.invoke(targetId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            entry.setValue(cart.merge(source));
            return null;
        });

        return true;
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
