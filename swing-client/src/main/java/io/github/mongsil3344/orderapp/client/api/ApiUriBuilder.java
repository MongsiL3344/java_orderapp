package io.github.mongsil3344.orderapp.client.api;

import java.net.URI;

// API 요청에 사용할 URI를 만들어주는 유틸 클래스
final class ApiUriBuilder {

    // 기본 서버 주소
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    // 객체 생성을 막기 위한 private 생성자
    private ApiUriBuilder() {
    }

    // 문자열로 받은 서버주소와 API 경로를 합쳐 URI객체를 반환하는 메서드
    static URI toUri(String baseUrl, String path) {
        String normalized = baseUrl == null ? "" : baseUrl.trim();
        if (normalized.isBlank()) {
            normalized = DEFAULT_BASE_URL;
        }
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }
        if (normalized.endsWith(path)) {
            return URI.create(normalized);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return URI.create(normalized + path);
    }
}
