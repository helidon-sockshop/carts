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
@Path("/carts/async")
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
