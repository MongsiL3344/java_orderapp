package io.github.mongsil3344.backend.order;

import java.time.Instant;

// Order 객체 엔티티
public class Order {

    // Field
    private final Long id;

    private final String customerName;

    private final int totalPrice;

    private final Instant createdAt;

    private final String orderJson;

    // Id가 없을 경우의 생성자
    public Order(String customerName, int totalPrice, Instant createdAt, String orderJson) {
        this(null, customerName, totalPrice, createdAt, orderJson);
    }

    // AllArgsConstructor
    public Order(Long id, String customerName, int totalPrice, Instant createdAt, String orderJson) {
        this.id = id;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.orderJson = orderJson;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getOrderJson() {
        return orderJson;
    }
}
