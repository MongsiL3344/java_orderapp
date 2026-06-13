package io.github.mongsil3344.backend.order;

import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    // 스프링 의존성 주입
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    // 생성자
    // 객체 주입
    public OrderService(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    // OrderResopnse형 createOrder 메서드
    public OrderResponse createOrder(JsonNode orderJson) throws JacksonException {
        String customerName = orderJson.path("customerName").asString("이름 없음");
        int totalPrice = orderJson.path("totalPrice").intValue(0);
        Order order = new Order(
                customerName,
                totalPrice,
                Instant.now(),
                objectMapper.writeValueAsString(orderJson)
        );

        return OrderResponse.from(orderRepository.save(order));
    }

    // OrderREsponse형 List를 반환하는 getORders메서드
    public List<OrderResponse> getOrders() {
        return orderRepository.findAllByOrderByIdDesc()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }
}
