package io.github.mongsil3344.backend.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // 서비스 레이어 빈 주입
    private final OrderService orderService;

    // 서비스 레이어 객체 주입
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // OrderResponse 객체를 반환하는 createOrder 메서드
    // menu와 마찬가지로 Http요청 바디에 있는 바디를 읽기 위해 어노테이션 사용
    // JacksonException을 던질 수 있음을 명시해서 컴파일러 경고를 제거 (try catch로 처리하지 않고 스프링이 잡아서 처리하도록 함)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody JsonNode orderJson) throws JacksonException {
        return orderService.createOrder(orderJson);
    }

    // OrderResponse 형 List 자료구조를 반환하는 getOrders 메서드
    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }
}
