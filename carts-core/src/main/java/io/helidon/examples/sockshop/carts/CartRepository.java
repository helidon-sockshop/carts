package io.helidon.examples.sockshop.carts;

import java.util.List;

/**
 * A repository interface that should be implemented by
 * the various data store integrations.
 */
public interface CartRepository {
    Cart getOrCreateCart(String cartId);
    void deleteCart(String cartId);
    boolean mergeCarts(String targetId, String sourceId);

    List<Item> getItems(String cartId);
    Item addItem(String cartId, Item item);
    Item getItem(String cartId, String itemId);
    Item updateItem(String cartId, Item item);
    void deleteItem(String cartId, String itemId);
}
