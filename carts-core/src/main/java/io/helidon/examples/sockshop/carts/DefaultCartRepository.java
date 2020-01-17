package io.helidon.examples.sockshop.carts;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * Default in-memory implementation that can be used for testing.
 */
@ApplicationScoped
public class DefaultCartRepository implements CartRepository {
    private Map<String, Cart> carts = new ConcurrentHashMap<>();

    @Override
    public Cart getOrCreateCart(String cartId) {
        return carts.computeIfAbsent(cartId, Cart::new);
    }

    @Override
    public void deleteCart(String cartId) {
        carts.remove(cartId);
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
        return getOrCreateCart(cartId).items();
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
