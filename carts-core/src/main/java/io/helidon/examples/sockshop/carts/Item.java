package io.helidon.examples.sockshop.carts;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {
    private String itemId;
    private int quantity;
    private float unitPrice;

    public Item(String itemId, int quantity, float unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Item() {
        this("", 1, 0F);
    }

    public Item(String itemId) {
        this(itemId, 1, 0F);
    }

    public Item(Item item) {
        this(item.itemId, item.quantity, item.unitPrice);
    }

    public Item(Item item, int quantity) {
        this(item.itemId, quantity, item.unitPrice);
    }

    public String itemId() {
        return itemId;
    }

    public int quantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return Objects.equals(itemId, item.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    // ****** Crappy getter/setters for Jackson JSON invoking ********

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }
}
