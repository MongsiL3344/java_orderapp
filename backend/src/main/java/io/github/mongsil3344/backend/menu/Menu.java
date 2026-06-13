package io.github.mongsil3344.backend.menu;

// 메뉴 객체 엔티티
public class Menu {

    // field
    private final Long id;

    private final String name;

    private final int price;

    private final String description;

    // id가 없을 경우의 constructor
    public Menu(String name, int price, String description) {
        this(null, name, price, description);
    }

    // AllArgsConstructor
    public Menu(Long id, String name, int price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Getter 메서드
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
