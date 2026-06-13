package io.github.mongsil3344.backend.menu;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MenuDataInitializer {

    @Bean
    ApplicationRunner initializeMenus(MenuRepository menuRepository, ObjectMapper objectMapper) {
        return args -> {
            if (menuRepository.count() > 0) {
                return;
            }

            menuRepository.saveAll(loadMenus(objectMapper));
        };
    }

    private List<Menu> loadMenus(ObjectMapper objectMapper) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/menus.json");
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            if (!root.isArray()) {
                throw new IllegalStateException("data/menus.json must contain a JSON array.");
            }

            List<Menu> menus = new ArrayList<>();
            for (JsonNode menuNode : root) {
                menus.add(new Menu(
                        menuNode.path("name").asString(),
                        menuNode.path("price").intValue(),
                        menuNode.path("description").asString()
                ));
            }
            return menus;
        }
    }
}
