package io.github.mongsil3344.orderapp.client.model;

import java.util.List;

public record OrderRequest(
        String customerName,
        String phoneNumber,
        String address,
        String orderedAt,
        int totalPrice,
        List<OrderItem> items
) {

    public OrderRequest {
        items = List.copyOf(items);
    }
}
