package io.github.mongsil3344.backend.menu;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;

@Repository
public class MenuRepository {

    // JdbcTEmplate의 메서드를 사용하기 위해 선언
    private final JdbcTemplate jdbcTemplate;

    // 생성자
    // 스프링 DI
    public MenuRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 메뉴 추가 메서드
    // 생성된 Menu객체를 반환함
    public Menu save(Menu menu) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(
                    "insert into menus (name, price, description) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, menu.getName());
            statement.setInt(2, menu.getPrice());
            statement.setString(3, menu.getDescription());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long id = key == null ? null : key.longValue();
        return new Menu(id, menu.getName(), menu.getPrice(), menu.getDescription());
    }

    // 메뉴 전체 조회 메서드
    // Menu타입 List 자료구조를 반환
    public List<Menu> findAllByOrderByIdAsc() {
        return jdbcTemplate.query(
                "select id, name, price, description from menus order by id asc",
                (resultSet, rowNumber) -> new Menu(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("price"),
                        resultSet.getString("description")
                )
        );
    }

    // boolean을 반환하는 메뉴 수정 메서드
    // 수정된 행의 개수가 0보다 클때 (수정된것이 존재할떄) true를 반환함
    public boolean update(Menu menu) {
        int updatedRows = jdbcTemplate.update(
                "update menus set name = ?, price = ?, description = ? where id = ?",
                menu.getName(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getId()
        );
        return updatedRows > 0;
    }

    // boolean을 반환하는 메뉴 삭제 메서드
    public boolean deleteById(Long id) {
        int deletedRows = jdbcTemplate.update("delete from menus where id = ?", id);
        return deletedRows > 0;
    }
}
