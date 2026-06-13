package io.github.mongsil3344.orderapp.client.model;

public record OrderHistoryItem(
        Long id,
        String customerName,
        int totalPrice,
        String createdAt,
        String orderJson
) {
}
