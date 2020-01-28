package io.helidon.examples.sockshop.carts;

import java.io.Serializable;

import lombok.Data;

/**
 * Composite key for the {@link Item class} when using JPA.
 */
@Data
public class ItemId implements Serializable {
    private String itemId;
    private String cart;
}
