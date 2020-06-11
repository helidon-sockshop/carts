/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.jpa;

import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.Item;

import org.eclipse.microprofile.opentracing.Traced;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses relational database (via JPA) as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class JpaCartRepository implements CartRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Cart getOrCreateCart(String customerId) {
        Cart cart = em.find(Cart.class, customerId);
        return cart == null ? new Cart(customerId) : cart;
    }

    @Override
    @Transactional
    public void deleteCart(String customerId) {
        Cart cart = em.find(Cart.class, customerId);
        if (cart != null) {
            em.remove(cart);
        }
    }

    @Override
    @Transactional
    public boolean mergeCarts(String targetId, String sourceId) {
        Cart source = em.find(Cart.class, sourceId);
        if (source != null) {
            Cart target = getOrCreateCart(targetId);
            target.merge(source);
            em.remove(source);
            em.persist(target);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public List<Item> getItems(String cartId) {
        return getOrCreateCart(cartId).getItems();
    }

    @Override
    @Transactional
    public Item addItem(String cartId, Item item) {
        Cart cart = getOrCreateCart(cartId);
        item = cart.add(item);
        em.persist(cart);
        em.persist(item);
        return item;
    }

    @Override
    @Transactional
    public Item getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).getItem(itemId);
    }

    @Override
    @Transactional
    public Item updateItem(String cartId, Item item) {
        Cart cart = getOrCreateCart(cartId);
        item = cart.update(item);
        em.persist(cart);
        em.persist(item);
        return item;
    }

    @Override
    @Transactional
    public void deleteItem(String cartId, String itemId) {
        Cart cart = getOrCreateCart(cartId);
        cart.remove(itemId);
        em.persist(cart);
    }
}
