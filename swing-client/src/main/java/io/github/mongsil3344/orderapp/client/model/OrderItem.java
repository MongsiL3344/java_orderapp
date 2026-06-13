package io.github.mongsil3344.orderapp.client.model;

// 주문 요청 안에 들어가는 주문 메뉴 DTO
public record OrderItem(
        String menuName,
        int price,
        int quantity
) {
}
