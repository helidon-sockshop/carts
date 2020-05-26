/*
 *  Copyright (c) 2020 Oracle and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.helidon.examples.sockshop.carts.mongo;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.Item;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;

import org.eclipse.microprofile.opentracing.Traced;

import static com.mongodb.client.model.Filters.eq;
import static javax.interceptor.Interceptor.Priority.APPLICATION;

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses MongoDB as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class MongoCartRepository implements CartRepository {

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
