package io.github.mongsil3344.orderapp.client.model;

// 사장님 화면의 주문 내역 조회에 사용되는 DTO
public record OrderHistoryItem(
        Long id,
        String customerName,
        int totalPrice,
        String createdAt,
        String orderJson
) {
}
