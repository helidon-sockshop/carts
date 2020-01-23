package io.helidon.examples.sockshop.carts.mongo;

import javax.json.bind.annotation.JsonbTransient;

import io.helidon.examples.sockshop.carts.Cart;

import org.bson.types.ObjectId;

/**
 * @author Aleksandar Seovic  2020.01.16
 */
public class MongoCart extends Cart {
    @JsonbTransient
    public ObjectId _id;

    public MongoCart() {
    }

    public MongoCart(String customerId) {
        super(customerId);
    }

    @Override
    public String toString() {
        return "MongoCart{" +
                "id=" + _id +
                ", customerId='" + customerId + '\'' +
                ", items=" + items() +
                '}';
    }
}
