package io.github.mongsil3344.backend.menu;

// 메뉴 관련 api의 응답에 쓰이는 DTO
public record MenuResponse(
        Long id,
        String name,
        int price,
        String description
) {

    // 정적 팩토리 메서드
    // Menu 객체를 받아서 응답객체를 생성함
    // 직접 객체 생성을 할때 발생할 수 있는 실수를 방지하기 위해 객체를 받아서 내부적으로 만들도록 함
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getDescription()
        );
    }
}
