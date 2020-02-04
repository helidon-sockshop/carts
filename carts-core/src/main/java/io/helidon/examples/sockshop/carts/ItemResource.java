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
 * @author Aleksandar Seovic  2019.08.20
 */
public class ItemResource {

    private final CartRepository carts;
    private final String cartId;

    public ItemResource(CartRepository carts, String cartId) {
        this.carts = carts;
        this.cartId = cartId;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Item> getItems() {
        return carts.getItems(cartId);
    }

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

    @GET
    @Path("{itemId}")
    @Produces(APPLICATION_JSON)
    public Item getItem(@PathParam("itemId") String itemId) {
        return carts.getItem(cartId, itemId);
    }

    @DELETE
    @Path("{itemId}")
    public Response deleteItem(@PathParam("itemId") String itemId) {
        carts.deleteItem(cartId, itemId);
        return Response.accepted().build();
    }

    @PATCH
    @Consumes(APPLICATION_JSON)
    public Response updateItem(Item item) {
        carts.updateItem(cartId, item);
        return Response.accepted().build();
    }
}
