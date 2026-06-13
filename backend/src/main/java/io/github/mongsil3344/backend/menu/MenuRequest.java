package io.github.mongsil3344.backend.menu;

public record MenuRequest(
        String name,
        int price,
        String description
) {
}
