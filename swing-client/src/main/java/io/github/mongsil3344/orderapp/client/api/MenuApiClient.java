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
}
