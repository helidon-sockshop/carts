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

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Implementation of the Cart Service REST API.
 */
@ApplicationScoped
@Path("/carts-async")
public class CartResourceAsync implements CartApiAsync {

    @Inject
    private CartRepositoryAsync carts;

    @Override
    public CompletionStage<Cart> getCart(String customerId) {
        return carts.getOrCreateCart(customerId);
    }

    @Override
    public CompletionStage<Response> deleteCart(String customerId) {
        return carts.deleteCart(customerId)
                .thenApply(o -> Response.accepted().build());
    }

    @Override
    public CompletionStage<Response> mergeCarts(String customerId, String sessionId) {
        return carts.mergeCarts(customerId, sessionId)
                .thenApply(fMerged ->
                       fMerged
                        ? Response.accepted().build()
                        : Response.status(Response.Status.NOT_FOUND).build());
    }

    @Override
    public ItemsApiAsync getItems(String customerId) {
        return new ItemsResourceAsync(carts, customerId);
    }
}
