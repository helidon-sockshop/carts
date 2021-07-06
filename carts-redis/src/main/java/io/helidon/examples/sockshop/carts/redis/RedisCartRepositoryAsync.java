/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.redis;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.CartRepositoryAsync;
import io.helidon.examples.sockshop.carts.Item;

import org.eclipse.microprofile.opentracing.Traced;
import org.redisson.api.RMapAsync;

import static javax.interceptor.Interceptor.Priority.APPLICATION;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses Redis (via Redisson) as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class RedisCartRepositoryAsync implements CartRepositoryAsync {
    private RMapAsync<String, Cart> carts;

    @Inject
    public RedisCartRepositoryAsync(RMapAsync<String, Cart> carts) {
        this.carts = carts;
    }

    @Override
    public CompletionStage<Cart> getOrCreateCart(String customerId) {
        Cart cart = new Cart(customerId);
        return carts.putIfAbsentAsync(customerId, cart)
                .thenApply(existing -> existing != null ? existing : cart);
    }

    @Override
    public CompletionStage<Void> deleteCart(String customerId) {
        return carts.removeAsync(customerId).thenAccept(cart -> {});
    }

    @Override
    public CompletionStage<Boolean> mergeCarts(String targetId, String sourceId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        carts.removeAsync(sourceId)
                .whenComplete((source, t1) -> {
                    if (t1 != null) future.completeExceptionally(t1);
                    else if (source == null) future.complete(false);
                    else {
                        getOrCreateCart(targetId).whenComplete((target, t2) -> {
                            if (t2 != null) future.completeExceptionally(t2);
                            else {
                                target.merge(source);
                                carts.fastPutAsync(targetId, target).whenComplete((b, t3) -> {
                                    if (t3 != null) future.completeExceptionally(t3);
                                    else future.complete(true);
                                });
                            }
                        });
                    }
                });

        return future;
    }

    @Override
    public CompletionStage<Item> getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).thenApply(cart -> cart.getItem(itemId));
    }

    @Override
    public CompletionStage<List<Item>> getItems(String cartId) {
        return getOrCreateCart(cartId).thenApply(Cart::getItems);
    }

    @Override
    public CompletionStage<Item> addItem(String cartId, Item item) {
        CompletableFuture<Item> future = new CompletableFuture<>();

        getOrCreateCart(cartId).whenComplete((cart, t1) -> {
            if (t1 != null) future.completeExceptionally(t1);
            else {
                Item result = cart.add(item);
                carts.fastPutAsync(cartId, cart).whenComplete((b, t2) -> {
                    if (t2 != null) future.completeExceptionally(t2);
                    else future.complete(result);
                });
            }
        });

        return future;
    }

    @Override
    public CompletionStage<Item> updateItem(String cartId, Item item) {
        CompletableFuture<Item> future = new CompletableFuture<>();

        getOrCreateCart(cartId).whenComplete((cart, t1) -> {
            if (t1 != null) future.completeExceptionally(t1);
            else {
                Item result = cart.update(item);
                carts.fastPutAsync(cartId, cart).whenComplete((b, t2) -> {
                    if (t2 != null) future.completeExceptionally(t2);
                    else future.complete(result);
                });
            }
        });

        return future;
    }

    @Override
    public CompletionStage<Void> deleteItem(String cartId, String itemId) {
        return getOrCreateCart(cartId)
                .thenApply(cart -> cart.remove(itemId))
                .thenApply(cart -> carts.fastPutAsync(cartId, cart))
                .thenCompose(future -> future.thenAccept(aBoolean -> {}));
    }
}
