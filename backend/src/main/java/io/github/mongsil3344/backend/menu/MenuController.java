package io.github.mongsil3344.backend.menu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuRepository menuRepository;

    public MenuController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @GetMapping
    public List<MenuResponse> getMenus() {
        return menuRepository.findAllByOrderByIdAsc()
                .stream()
                .map(MenuResponse::from)
                .toList();
    }
}
