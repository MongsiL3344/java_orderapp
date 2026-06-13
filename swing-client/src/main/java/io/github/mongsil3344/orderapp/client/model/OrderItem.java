package io.github.mongsil3344.orderapp.client.model;

public record OrderItem(
        String menuName,
        int price,
        int quantity
) {
}
