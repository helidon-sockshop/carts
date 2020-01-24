package io.helidon.examples.sockshop.carts;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * Simple in-memory implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that can be used for demos and testing.
 * <p/>
 * This implementation is obviously not suitable for production use, because it is not
 * persistent and it doesn't scale, but it is trivial to write and very useful for the
 * API testing and quick demos.
 */
@ApplicationScoped
public class DefaultCartRepository implements CartRepository {
    private Map<String, Cart> carts = new ConcurrentHashMap<>();

    @Override
    public Cart getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, Cart::new);
    }

    @Override
    public void deleteCart(String customerId) {
        carts.remove(customerId);
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        Cart source = carts.remove(sourceId);
        if (source == null) {
            return false;
        }

        Cart target = getOrCreateCart(targetId);
        target.merge(source);
        return true;
    }

    @Override
    public List<Item> getItems(String cartId) {
        return getOrCreateCart(cartId).getItems();
    }

    @Override
    public Item addItem(String cartId, Item item) {
        return getOrCreateCart(cartId).add(item);
    }

    @Override
    public Item getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).getItem(itemId);
    }

    @Override
    public Item updateItem(String cartId, Item item) {
        return getOrCreateCart(cartId).update(item);
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        getOrCreateCart(cartId).remove(itemId);
    }
}
