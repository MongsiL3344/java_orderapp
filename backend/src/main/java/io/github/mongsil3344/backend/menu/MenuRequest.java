package io.github.mongsil3344.backend.menu;

// 메뉴 관련 api 요청에 사용되는 DTO(Data Transfer Object)
// 클래스로 선언해도 되지만 개발 편의성을 위해 record로 선언
public record MenuRequest(
        String name,
        int price,
        String description
) {
}
