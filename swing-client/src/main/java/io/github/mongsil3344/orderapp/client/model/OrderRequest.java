package io.github.mongsil3344.orderapp.client.model;

import java.util.List;

// 주문 생성 요청에 사용되는 DTO
public record OrderRequest(
        String customerName,
        String phoneNumber,
        String address,
        String orderedAt,
        int totalPrice,
        List<OrderItem> items
) {

    // 생성자
    // 주문 메뉴 목록이 외부에서 수정되지 않도록 복사해서 저장함
    public OrderRequest {
        items = List.copyOf(items);
    }
}
