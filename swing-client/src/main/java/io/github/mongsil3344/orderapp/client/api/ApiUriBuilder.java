package io.github.mongsil3344.orderapp.client.api;

import java.net.URI;

final class ApiUriBuilder {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private ApiUriBuilder() {
    }

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
