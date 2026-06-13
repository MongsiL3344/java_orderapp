package io.github.mongsil3344.orderapp.client.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mongsil3344.orderapp.client.model.MenuItem;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class MenuApiClient {

    private static final String MENUS_PATH = "/api/menus";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MenuApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<MenuItem> fetchMenus(String baseUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(ApiUriBuilder.toUri(baseUrl, MENUS_PATH))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("메뉴 조회 실패: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), new TypeReference<>() {
        });
    }

    public MenuItem createMenu(String baseUrl, MenuItem menuItem) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(ApiUriBuilder.toUri(baseUrl, MENUS_PATH))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(toRequestBody(menuItem), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = send(request);
        return objectMapper.readValue(response.body(), MenuItem.class);
    }

    public MenuItem updateMenu(String baseUrl, MenuItem menuItem) throws IOException, InterruptedException {
        if (menuItem.id() == null) {
            throw new IOException("수정할 메뉴 ID가 없습니다.");
        }

        HttpRequest request = HttpRequest.newBuilder(ApiUriBuilder.toUri(baseUrl, MENUS_PATH + "/" + menuItem.id()))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(toRequestBody(menuItem), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = send(request);
        return objectMapper.readValue(response.body(), MenuItem.class);
    }

    public void deleteMenu(String baseUrl, Long id) throws IOException, InterruptedException {
        if (id == null) {
            throw new IOException("삭제할 메뉴 ID가 없습니다.");
        }

        HttpRequest request = HttpRequest.newBuilder(ApiUriBuilder.toUri(baseUrl, MENUS_PATH + "/" + id))
                .timeout(Duration.ofSeconds(5))
                .DELETE()
                .build();

        send(request);
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("메뉴 요청 실패: HTTP " + response.statusCode() + "\n" + response.body());
        }
        return response;
    }

    private String toRequestBody(MenuItem menuItem) throws IOException {
        return objectMapper.writeValueAsString(new MenuPayload(
                menuItem.name(),
                menuItem.price(),
                menuItem.description()
        ));
    }

    private record MenuPayload(
            String name,
            int price,
            String description
    ) {
    }
}
