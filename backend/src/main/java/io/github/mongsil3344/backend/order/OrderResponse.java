package io.github.mongsil3344.backend.order;

import java.time.Instant;

// OrderReponse DTO
// 메뉴와 마찬가지로 편의성을 위해 record사용
public record OrderResponse(
        Long id,
        String customerName,
        int totalPrice,
        Instant createdAt,
        String orderJson
) {

    // 정적 팩토리 메서드
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
