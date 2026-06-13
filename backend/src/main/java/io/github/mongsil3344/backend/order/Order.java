package io.github.mongsil3344.backend.order;

import java.time.Instant;

public class Order {

    private final Long id;

    private final String customerName;

    private final int totalPrice;

    private final Instant createdAt;

    private final String orderJson;

    public Order(String customerName, int totalPrice, Instant createdAt, String orderJson) {
        this(null, customerName, totalPrice, createdAt, orderJson);
    }

    public Order(Long id, String customerName, int totalPrice, Instant createdAt, String orderJson) {
        this.id = id;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.orderJson = orderJson;
    }

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
