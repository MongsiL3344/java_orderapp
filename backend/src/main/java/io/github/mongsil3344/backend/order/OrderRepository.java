package io.github.mongsil3344.backend.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderRepository {

    // JdbcTemplate 의존성 주입
    private final JdbcTemplate jdbcTemplate;

    // 생성자, 객체주입
    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 주문객체 생성하는 save메서드
    public Order save(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(
                    """
                            insert into customer_orders (customer_name, total_price, created_at, order_json)
                            values (?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, order.getCustomerName());
            statement.setInt(2, order.getTotalPrice());
            statement.setTimestamp(3, Timestamp.from(order.getCreatedAt()));
            statement.setString(4, order.getOrderJson());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long id = key == null ? null : key.longValue();
        return new Order(
                id,
                order.getCustomerName(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getOrderJson()
        );
    }

    // Order형 list 반환하는 전체조회 메서드
    public List<Order> findAllByOrderByIdDesc() {
        return jdbcTemplate.query(
                """
                        select id, customer_name, total_price, created_at, order_json
                        from customer_orders
                        order by id desc
                        """,
                (resultSet, rowNumber) -> new Order(
                        resultSet.getLong("id"),
                        resultSet.getString("customer_name"),
                        resultSet.getInt("total_price"),
                        resultSet.getTimestamp("created_at").toInstant(),
                        resultSet.getString("order_json")
                )
        );
    }
}
