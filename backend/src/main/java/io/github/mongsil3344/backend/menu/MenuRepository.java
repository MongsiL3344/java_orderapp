package io.github.mongsil3344.backend.menu;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;

@Repository
public class MenuRepository {

    private final JdbcTemplate jdbcTemplate;

    public MenuRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public boolean deleteById(Long id) {
        int deletedRows = jdbcTemplate.update("delete from menus where id = ?", id);
        return deletedRows > 0;
    }
}
