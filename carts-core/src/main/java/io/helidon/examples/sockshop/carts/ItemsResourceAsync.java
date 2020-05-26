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
import java.util.concurrent.CompletionStage;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Implementation of Items sub-resource REST API.
 */
public class ItemsResourceAsync implements ItemsApiAsync {

    private final CartRepositoryAsync carts;
    private final String cartId;

    ItemsResourceAsync(CartRepositoryAsync carts, String cartId) {
        this.carts = carts;
        this.cartId = cartId;
    }

    @Override
    public CompletionStage<List<Item>> getItems() {
        return carts.getItems(cartId);
    }

    @Override
    public CompletionStage<Response> addItem(Item item) {
        if (item.getQuantity() == 0) {
            item.setQuantity(1);
        }

        return carts.addItem(cartId, item)
                    .thenApply(result -> Response.status(Status.CREATED).entity(result).build());
    }

    @Override
    public CompletionStage<Response> getItem(String itemId) {
        return carts.getItem(cartId, itemId)
                    .thenApply(item ->
                            item == null
                            ? Response.status(Status.NOT_FOUND).build()
                            : Response.ok(item).build());
    }

    @Override
    public CompletionStage<Response> deleteItem(String itemId) {
        return carts.deleteItem(cartId, itemId)
                .thenApply(ignore -> Response.accepted().build());
    }

    @Override
    public CompletionStage<Response> updateItem(Item item) {
        return carts.updateItem(cartId, item)
                .thenApply(ignore -> Response.accepted().build());
    }
}
