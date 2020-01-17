package io.helidon.examples.sockshop.carts.mongo;

import io.helidon.examples.sockshop.carts.Cart;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

/**
 * @author Aleksandar Seovic  2020.01.16
 */
public class MongoCart extends Cart {
    @BsonId
    public ObjectId id;

    public MongoCart() {
    }

    public MongoCart(String customerId) {
        super(customerId);
    }

    @Override
    public String toString() {
        return "MongoCart{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", items=" + items() +
                '}';
    }
}
