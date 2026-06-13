package io.github.mongsil3344.orderapp.client.api;

/**
 * 주문결과 DTO
 * @param statusCode
 * @param responseBody
 * @param requestBody
 */
public record OrderApiResponse(
        int statusCode,
        String responseBody,
        String requestBody
) {

    // 상태코드가 2XX면 성공처리
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
}
