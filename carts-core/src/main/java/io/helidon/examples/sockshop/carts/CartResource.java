package io.helidon.examples.sockshop.carts;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Implementation of Cart Service REST API.
 */
@ApplicationScoped
@Path("/carts")
public class CartResource {

    @Inject
    private CartRepository carts;

    @GET
    @Path("{customerId}")
    @Produces(APPLICATION_JSON)
    public Cart getCart(@PathParam("customerId") String customerId) {
        return carts.getOrCreateCart(customerId);
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCart(@PathParam("customerId") String customerId) {
        carts.deleteCart(customerId);
        return Response.accepted().build();
    }

    @GET
    @Path("{customerId}/merge")
    public Response mergeCarts(@PathParam("customerId") String customerId,
                               @QueryParam("sessionId") String sessionId) {
        boolean fMerged = carts.mergeCarts(customerId, sessionId);
        return fMerged
                ? Response.accepted().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("{customerId}/items")
    public ItemResource getItems(@PathParam("customerId") String customerId) {
        return new ItemResource(carts, customerId);
    }
}
