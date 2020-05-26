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

package io.helidon.examples.sockshop.carts;

import java.util.List;

/**
 * Helper class that blocks on async repository calls, allowing
 * us to reuse the tests for both sync and async repository implementations.
 */
public class SyncCartRepository implements CartRepository {
    private CartRepositoryAsync carts;

    public SyncCartRepository(CartRepositoryAsync carts) {
        this.carts = carts;
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        return carts.getOrCreateCart(customerId).toCompletableFuture().join();
    }

    @Override
    public void deleteCart(String customerId) {
        carts.deleteCart(customerId).toCompletableFuture().join();
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        return carts.mergeCarts(targetId, sourceId).toCompletableFuture().join();
    }

    @Override
    public Item getItem(String cartId, String itemId) {
        return carts.getItem(cartId, itemId).toCompletableFuture().join();
    }

    @Override
    public List<Item> getItems(String cartId) {
        return carts.getItems(cartId).toCompletableFuture().join();
    }

    @Override
    public Item addItem(String cartId, Item item) {
        return carts.addItem(cartId, item).toCompletableFuture().join();
    }

    @Override
    public Item updateItem(String cartId, Item item) {
        return carts.updateItem(cartId, item).toCompletableFuture().join();
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        carts.deleteItem(cartId, itemId).toCompletableFuture().join();
    }
}
