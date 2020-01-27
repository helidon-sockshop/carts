package io.helidon.examples.sockshop.carts.mongo;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.DefaultCartRepository;
import io.helidon.examples.sockshop.carts.Item;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import static com.mongodb.client.model.Filters.eq;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses MongoDB as a backend data store.
 */
@ApplicationScoped
@Specializes
public class MongoCartRepository extends DefaultCartRepository {

    private MongoCollection<Cart> carts;

    @Inject
    MongoCartRepository(MongoCollection<Cart> carts) {
        this.carts = carts;
    }

    @PostConstruct
    void configure() {
        carts.createIndex(Indexes.hashed("customerId"));
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        Cart cart = carts.find(eq("customerId", customerId)).first();
        if (cart == null) {
            cart = new Cart(customerId);
            carts.insertOne(cart);
        }
        return cart;
    }

    @Override
    public void deleteCart(String customerId) {
        carts.deleteOne(eq("customerId", customerId));
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        Cart source = carts.findOneAndDelete(eq("customerId", sourceId));
        if (source != null) {
            Cart target = getOrCreateCart(targetId);
            target.merge(source);
            carts.replaceOne(eq("customerId", targetId), target);
            return true;
        }
        return false;
    }

    @Override
    public List<Item> getItems(String cartId) {
        return getOrCreateCart(cartId).getItems();
    }

    @Override
    public Item getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).getItem(itemId);
    }

    @Override
    public Item addItem(String cartId, Item item) {
        Cart cart = getOrCreateCart(cartId);

        Item result = cart.add(item);
        carts.replaceOne(eq("customerId", cartId), cart);
        return result;
    }

    @Override
    public Item updateItem(String cartId, Item item) {
        Cart cart = getOrCreateCart(cartId);
        Item result = cart.update(item);
        carts.replaceOne(eq("customerId", cartId), cart);
        return result;
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        Cart cart = getOrCreateCart(cartId);
        cart.remove(itemId);
        carts.replaceOne(eq("customerId", cartId), cart);
    }
}
