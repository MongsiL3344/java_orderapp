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

// 메뉴 관련 API를 호출하는 클라이언트 클래스
public class MenuApiClient {

    // 메뉴 API 공통 경로
    private static final String MENUS_PATH = "/api/menus";

    // HTTP 요청과 JSON 변환에 필요한 객체
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // 생성자
    // HTTP 클라이언트와 ObjectMapper 객체를 초기화함
    public MenuApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // MenuItem형 List 자료구조를 반환하는 메뉴 전체 조회 메서드
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

    // MenuItem 객체를 반환하는 메뉴 추가 요청 메서드
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

    // MenuItem 객체를 반환하는 메뉴 수정 요청 메서드
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

    // 반환값이 없는 메뉴 삭제 요청 메서드
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

    // 공통 HTTP 요청 전송 메서드
    // 2XX 응답이 아닌 경우 IOException을 던져서 호출한 쪽에서 처리하도록 함
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

    // MenuItem 객체를 서버 요청 바디 JSON문자열로 변환하는 메서드
    private String toRequestBody(MenuItem menuItem) throws IOException {
        return objectMapper.writeValueAsString(new MenuPayload(
                menuItem.name(),
                menuItem.price(),
                menuItem.description()
        ));
    }

    // 메뉴 추가, 수정 요청 바디에 사용되는 DTO
    private record MenuPayload(
            String name,
            int price,
            String description
    ) {
    }
}
