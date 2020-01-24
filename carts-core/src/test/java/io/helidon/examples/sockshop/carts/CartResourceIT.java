package io.helidon.examples.sockshop.carts;

import io.helidon.microprofile.server.Server;

import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link io.helidon.examples.sockshop.carts.CartResource}.
 */
public class CartResourceIT {
    private static final Server server = Server.builder().port(0).build().start();
    private CartRepository carts;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = server.port();
        //RestAssured.config = RestAssured.config().objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JSONB));

        carts = server.cdiContainer().select(CartRepository.class).get();
        carts.deleteCart("C1");
        carts.deleteCart("C2");
    }

    @Test
    void testGetCart() {
        when().
                get("/carts/{cartId}", "C1").
        then().
                statusCode(200).
                body("customerId", equalTo("C1"),
                     "items", nullValue());
    }

    @Test
    void testDeleteCart() {
        when().
                delete("/carts/{cartId}", "C1").
        then().
                statusCode(ACCEPTED.getStatusCode());
    }

    @Test
    void testMergeNonExistentCarts() {
        given().
                queryParam("sessionId", "FOO").
        when().
                get("/carts/{cartId}/merge", "C1").
        then().
                statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void testMergeCarts() {
        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C2", new Item("X1", 5, 10f));
        carts.addItem("C2", new Item("X2", 5, 10f));

        // it should succeed the first time
        given().
                queryParam("sessionId", "C2").
        when().
                get("/carts/{cartId}/merge", "C1").
        then().
                statusCode(ACCEPTED.getStatusCode());

        assertThat(carts.getItems("C1").size(), is(2));
        assertThat(carts.getItem("C1", "X1").getQuantity(), is(10));
        
        // now that it has been merged, it should fail
        given().
                queryParam("sessionId", "C2").
        when().
                get("/carts/{cartId}/merge", "C1").
        then().
                statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void testGetItems() {
        // should be empty to start
        when().
                get("/carts/{cartId}/items", "C1").
        then().
                statusCode(OK.getStatusCode()).
                body("$", empty());

        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C1", new Item("X2", 5, 10f));

        // now it should return two items
        when().
                get("/carts/{cartId}/items", "C1").
        then().
                statusCode(OK.getStatusCode()).
                body("itemId", hasItems("X1", "X2"));
    }

    @Test
    void testAddItem() {
        given().
                contentType(JSON).
                body(new Item("X1", 5, 10f)).
        when().
                post("/carts/{cartId}/items", "C1").
        then().
                statusCode(CREATED.getStatusCode()).
                body("itemId", is("X1"),
                     "quantity", is(5),
                     "unitPrice", is(10f));

        // if we do it again the quantity should increase
        given().
                contentType(JSON).
                body(new Item("X1", 3, 10f)).
        when().
                post("/carts/{cartId}/items", "C1").
        then().
                statusCode(CREATED.getStatusCode()).
                body("itemId", is("X1"),
                     "quantity", is(8),
                     "unitPrice", is(10f));
    }

    @Test
    void testUpdateItem() {
        given().
                contentType(JSON).
                body(new Item("X1", 5, 10f)).
        when().
                patch("/carts/{cartId}/items", "C1").
        then().
                statusCode(ACCEPTED.getStatusCode());

        assertThat(carts.getItem("C1", "X1").getQuantity(), is(5));

        // if we do it again the quantity should be overwritten
        given().
                contentType(JSON).
                body(new Item("X1", 3, 10f)).
        when().
                patch("/carts/{cartId}/items", "C1").
        then().
                statusCode(ACCEPTED.getStatusCode());

        assertThat(carts.getItem("C1", "X1").getQuantity(), is(3));
    }

    @Test
    void testGetItem() {
        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C1", new Item("X2", 3, 17f));

        when().
                get("/carts/{cartId}/items/{itemId}", "C1", "X1").
        then().
                statusCode(OK.getStatusCode()).
                body("itemId", is("X1"),
                     "quantity", is(5),
                     "unitPrice", is(10f));
    }

    @Test
    void testDeleteItem() {
        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C1", new Item("X2", 3, 17f));

        when().
                delete("/carts/{cartId}/items/{itemId}", "C1", "X1").
        then().
                statusCode(ACCEPTED.getStatusCode());

        assertThat(carts.getItems("C1").size(), is(1));
        assertThat(carts.getItem("C1", "X1"), nullValue());
    }
}
