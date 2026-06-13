package io.github.mongsil3344.backend.order;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String customerName,
        int totalPrice,
        Instant createdAt,
        String orderJson
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getOrderJson()
        );
    }
}
