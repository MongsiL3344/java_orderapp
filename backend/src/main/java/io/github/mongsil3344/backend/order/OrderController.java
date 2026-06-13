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
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public OrderController(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody JsonNode orderJson) throws JacksonException {
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

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderRepository.findAllByOrderByIdDesc()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }
}
