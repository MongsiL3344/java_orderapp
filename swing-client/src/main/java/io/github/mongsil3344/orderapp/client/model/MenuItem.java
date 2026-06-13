package io.github.mongsil3344.orderapp.client.model;

// 메뉴 정보를 화면과 API 통신에서 사용하는 DTO
public record MenuItem(
        Long id,
        String name,
        int price,
        String description
) {
}
