package io.github.mongsil3344.orderapp.client.model;

public class CartItem {

    private final MenuItem menuItem;
    private int quantity;

    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem menuItem() {
        return menuItem;
    }

    public int quantity() {
        return quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    public int lineTotal() {
        return menuItem.price() * quantity;
    }
}
