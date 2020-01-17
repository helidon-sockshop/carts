package io.helidon.examples.sockshop.carts.mongo;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import io.helidon.examples.sockshop.carts.DefaultCartRepository;
import io.helidon.examples.sockshop.carts.Item;

import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Aleksandar Seovic  2020.01.16
 */
@ApplicationScoped
@Specializes
public class MongoCartRepository extends DefaultCartRepository {
    private static final Logger LOGGER = Logger.getLogger(MongoCartRepository.class.getName());

    @Inject
    private MongoCollection<MongoCart> carts;

    @Override
    public MongoCart getOrCreateCart(String cartId) {
        MongoCart cart = carts.find(eq("customerId", cartId)).first();
        if (cart == null) {
            LOGGER.info("Creating cart " + cartId);
            cart = new MongoCart(cartId);
            carts.insertOne(cart);
        }
        return cart;
    }

    @Override
    public void deleteCart(String cartId) {
        LOGGER.info("Deleting cart " + cartId);
        carts.deleteOne(eq("customerId", cartId));
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        MongoCart source = carts.findOneAndDelete(eq("customerId", sourceId));
        if (source != null) {
            LOGGER.info("Merging cart " + sourceId + " into " + targetId);
            MongoCart target = getOrCreateCart(targetId);
            target.merge(source);
            carts.replaceOne(eq("customerId", targetId), target);
            return true;
        }
        return false;
    }

    @Override
    public List<Item> getItems(String cartId) {
        return getOrCreateCart(cartId).items();
    }

    @Override
    public Item getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).getItem(itemId);
    }

    @Override
    public Item addItem(String cartId, Item item) {
        MongoCart cart = getOrCreateCart(cartId);

        LOGGER.info("Adding " + item + " to cart " + cartId);
        Item result = cart.add(item);
        carts.replaceOne(eq("customerId", cartId), cart);
        return result;
    }

    @Override
    public Item updateItem(String cartId, Item item) {
        MongoCart cart = getOrCreateCart(cartId);
        Item result = cart.update(item);
        LOGGER.info("Updating " + item + " in cart " + cartId);
        carts.replaceOne(eq("customerId", cartId), cart);
        return result;
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        MongoCart cart = getOrCreateCart(cartId);
        cart.remove(itemId);
        LOGGER.info("Removing item " + itemId + " from cart " + cartId);
        carts.replaceOne(eq("customerId", cartId), cart);
    }
}
