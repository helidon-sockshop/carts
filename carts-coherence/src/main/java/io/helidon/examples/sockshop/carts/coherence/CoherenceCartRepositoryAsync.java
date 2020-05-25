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

import static javax.interceptor.Interceptor.Priority.APPLICATION;

import com.oracle.coherence.cdi.Cache;
import com.tangosol.net.AsyncNamedCache;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.CartRepositoryAsync;
import io.helidon.examples.sockshop.carts.Item;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.opentracing.Traced;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository} that that uses Coherence as a backend data
 * store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class CoherenceCartRepositoryAsync implements CartRepositoryAsync {
    protected final AsyncNamedCache<String, Cart> carts;

    @Inject
    CoherenceCartRepositoryAsync(@Cache("carts") AsyncNamedCache<String, Cart> carts) {
        this.carts = carts;
    }

    @Override
    public CompletionStage<Void> deleteCart(String customerId) {
        return carts.remove(customerId).thenAccept(cart -> {});
    }

    @Override
    public CompletionStage<Boolean> mergeCarts(String targetId, String sourceId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        carts.remove(sourceId)
                .whenComplete((source, t1) -> {
                    if (t1 != null) {
                        future.completeExceptionally(t1);
                    } else {
                        if (source == null) {
                            future.complete(false);
                        } else {
                            carts.invoke(targetId, entry -> {
                                Cart cart = entry.getValue(new Cart(entry.getKey()));
                                entry.setValue(cart.merge(source));
                                return null;
                            }).whenComplete((target, t2) -> {
                                if (t2 != null) {
                                    future.completeExceptionally(t2);
                                } else {
                                    future.complete(true);
                                }
                            });
                        }
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
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public CompletionStage<Item> updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public CompletionStage<Void> deleteItem(String cartId, String itemId) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        }).thenAccept(cart -> {});
    }

    @Override
    public CompletionStage<Cart> getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }
}
