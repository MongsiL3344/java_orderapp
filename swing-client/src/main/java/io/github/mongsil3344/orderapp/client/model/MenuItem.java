package io.github.mongsil3344.orderapp.client.model;

public record MenuItem(
        Long id,
        String name,
        int price,
        String description
) {
}
