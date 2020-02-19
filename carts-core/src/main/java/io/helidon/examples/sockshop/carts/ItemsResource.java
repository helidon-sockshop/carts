package io.helidon.examples.sockshop.carts;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Implementation of Items sub-resource REST API.
 */
public class ItemsResource implements ItemsApi {

    private final CartRepository carts;
    private final String cartId;

    public ItemsResource(CartRepository carts, String cartId) {
        this.carts = carts;
        this.cartId = cartId;
    }

    @Override
    @GET
    @Produces(APPLICATION_JSON)
    public List<Item> getItems() {
        return carts.getItems(cartId);
    }

    @Override
    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
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
    @GET
    @Path("{itemId}")
    @Produces(APPLICATION_JSON)
    public Response getItem(@PathParam("itemId") String itemId) {
        Item item = carts.getItem(cartId, itemId);
        return item == null
                ? Response.status(Status.NOT_FOUND).build()
                : Response.ok(item).build();
    }

    @Override
    @DELETE
    @Path("{itemId}")
    public Response deleteItem(@PathParam("itemId") String itemId) {
        carts.deleteItem(cartId, itemId);
        return Response.accepted().build();
    }

    @Override
    @PATCH
    @Consumes(APPLICATION_JSON)
    public Response updateItem(Item item) {
        carts.updateItem(cartId, item);
        return Response.accepted().build();
    }
}
