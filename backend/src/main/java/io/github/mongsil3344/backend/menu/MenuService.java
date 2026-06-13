package io.github.mongsil3344.backend.menu;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MenuService {

    // 메뉴 레포지토리 빈 주입
    private final MenuRepository menuRepository;

    // 생성자에 레포지토리 의존성 주입
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    // MenuResponse 타입 LIst 자료구조를 반환하는 getMenus 메서드
    public List<MenuResponse> getMenus() {
        return menuRepository.findAllByOrderByIdAsc()
                .stream()
                .map(MenuResponse::from)
                .toList();
    }

    // MenuResponse 객체를 반환하는 createMenu 메서드
    public MenuResponse createMenu(MenuRequest request) {
        validate(request);
        Menu menu = menuRepository.save(new Menu(
                request.name().trim(),
                request.price(),
                request.description().trim()
        ));
        return MenuResponse.from(menu);
    }

    // MenuREsponse 객체를 반환하는 updateMenu메서드
    public MenuResponse updateMenu(Long id, MenuRequest request) {
        validate(request);
        Menu menu = new Menu(
                id,
                request.name().trim(),
                request.price(),
                request.description().trim()
        );

        // 레포지토리에서 정상 처리가 되지 않았을 경우의 예외처리
        if (!menuRepository.update(menu)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다.");
        }
        return MenuResponse.from(menu);
    }

    // 메뉴 삭제 메서드
    public void deleteMenu(Long id) {
        if (!menuRepository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다.");
        }
    }

    // 유효성 검증 메서드
    // 반환값은 없고 예외처리를 통해 예외를 던지고 스프링mvc의 예외처리기가 잡아서 처리할 수 있도록함
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
