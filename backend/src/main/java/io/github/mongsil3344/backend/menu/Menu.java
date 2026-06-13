package io.github.mongsil3344.backend.menu;

public class Menu {

    private final Long id;

    private final String name;

    private final int price;

    private final String description;

    public Menu(String name, int price, String description) {
        this(null, name, price, description);
    }

    public Menu(Long id, String name, int price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

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
