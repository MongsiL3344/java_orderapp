package io.github.mongsil3344.backend.menu;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuRepository {

    private final JdbcTemplate jdbcTemplate;

    public MenuRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int count() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from menus", Integer.class);
        return count == null ? 0 : count;
    }

    public void saveAll(List<Menu> menus) {
        for (Menu menu : menus) {
            jdbcTemplate.update(
                    "insert into menus (name, price, description) values (?, ?, ?)",
                    menu.getName(),
                    menu.getPrice(),
                    menu.getDescription()
            );
        }
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
}
