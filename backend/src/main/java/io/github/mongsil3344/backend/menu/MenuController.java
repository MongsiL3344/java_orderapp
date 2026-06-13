package io.github.mongsil3344.backend.menu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuResponse createMenu(@RequestBody MenuRequest request) {
        validate(request);
        Menu menu = menuRepository.save(new Menu(
                request.name().trim(),
                request.price(),
                request.description().trim()
        ));
        return MenuResponse.from(menu);
    }

    @PutMapping("/{id}")
    public MenuResponse updateMenu(@PathVariable Long id, @RequestBody MenuRequest request) {
        validate(request);
        Menu menu = new Menu(
                id,
                request.name().trim(),
                request.price(),
                request.description().trim()
        );

        if (!menuRepository.update(menu)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다.");
        }
        return MenuResponse.from(menu);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable Long id) {
        if (!menuRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다.");
        }
    }

    private void validate(MenuRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메뉴 정보를 입력해주세요.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메뉴 이름을 입력해주세요.");
        }
        if (request.price() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격은 0원 이상이어야 합니다.");
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메뉴 설명을 입력해주세요.");
        }
    }
}
