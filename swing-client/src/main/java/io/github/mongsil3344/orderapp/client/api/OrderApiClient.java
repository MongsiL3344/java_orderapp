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

// 주문 관련 API를 호출하는 클라이언트 클래스
public class OrderApiClient {

    // 주문 API 공통 경로
    private static final String ORDERS_PATH = "/api/orders";

    // HTTP 요청과 JSON 변환에 필요한 객체
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // 생성자
    // HTTP 클라이언트와 ObjectMapper 객체를 초기화함
    public OrderApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // OrderApiResponse 객체를 반환하는 주문 생성 요청 메서드
    // 주문 요청 객체를 JSON으로 바꿔서 백엔드에 전송함
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

    // OrderHistoryItem형 List 자료구조를 반환하는 주문 전체 조회 메서드
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
