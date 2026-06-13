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

import java.util.List;

// 메뉴 관련 API의 컨트롤러 (엔드포인ㄴ트 진입점)
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    // 서비스 레이어 객체 의존성 주입
    // 수동으로 서비스레이어 객체를 다루지 않고 싱글톤 패턴으로 활용하기 위해 스프링 프레임워크에 빈으로 등록하여 자동 주입하도록 함 (Spring DI)
    private final MenuService menuService;

    // 받아온 의존성을 컨트롤러 객체에 주입
    // 해당 코드로 컨트롤러가 서비스에 의존하는 관계가 성립
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // MenuResponse형 List 자료구조를 반환하는 getMenus 메서드
    @GetMapping
    public List<MenuResponse> getMenus() {
        return menuService.getMenus();
    }

    // MenuResonse 객체를 반환하는 createMenu 메서드
    // @RequestBody 어노테이션을 붙여서 HTTP 요청 바디에 있는 값을 객체로 역직렬화 해서 읽을 수 있도록 함
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuResponse createMenu(@RequestBody MenuRequest request) {
        return menuService.createMenu(request);
    }

    // MenuResponse객체를 반환하는 updateMenu 메서드
    // query string으로 메뉴의 id값을 받을 수 있도록 설계
    @PutMapping("/{id}")
    public MenuResponse updateMenu(@PathVariable Long id, @RequestBody MenuRequest request) {
        return menuService.updateMenu(id, request);
    }

    // 반환값이 없는 deleteMenu메서드
    // query string으로 메뉴의 id값을 받을 수 있도록 설계
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }
}
