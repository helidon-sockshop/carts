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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Implementation of Items sub-resource REST API.
 */
public class ItemsResource implements ItemsApi {

    private final CartRepository carts;
    private final String cartId;

    ItemsResource(CartRepository carts, String cartId) {
        this.carts = carts;
        this.cartId = cartId;
    }

    @Override
    public List<Item> getItems() {
        return carts.getItems(cartId);
    }

    @Override
    public Response addItem(Item item) {
        if (item.getQuantity() == 0) {
            item.setQuantity(1);
        }
        Item result = carts.addItem(cartId, item);
        return Response
                .status(Status.CREATED)
                .entity(result)
                .build();
    }

    @Override
    public Response getItem(String itemId) {
        Item item = carts.getItem(cartId, itemId);
        return item == null
                ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(item).build();
    }

    @Override
    public Response deleteItem(String itemId) {
        carts.deleteItem(cartId, itemId);
        return Response.accepted().build();
    }

    @Override
    public Response updateItem(Item item) {
        carts.updateItem(cartId, item);
        return Response.accepted().build();
    }
}
