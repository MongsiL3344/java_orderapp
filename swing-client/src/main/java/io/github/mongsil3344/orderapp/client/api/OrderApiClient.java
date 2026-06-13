package io.github.mongsil3344.orderapp.client.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mongsil3344.orderapp.client.model.OrderHistoryItem;
import io.github.mongsil3344.orderapp.client.model.OrderRequest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class OrderApiClient {

    private static final String ORDERS_PATH = "/api/orders";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OrderApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public OrderApiResponse postOrder(String baseUrl, OrderRequest orderRequest)
            throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(orderRequest);
        HttpRequest request = HttpRequest.newBuilder(ApiUriBuilder.toUri(baseUrl, ORDERS_PATH))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        return new OrderApiResponse(response.statusCode(), response.body(), requestBody);
    }

    public List<OrderHistoryItem> fetchOrders(String baseUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(ApiUriBuilder.toUri(baseUrl, ORDERS_PATH))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("주문 조회 실패: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), new TypeReference<>() {
        });
    }
}
