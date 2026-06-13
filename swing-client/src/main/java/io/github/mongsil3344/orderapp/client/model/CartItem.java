package io.github.mongsil3344.orderapp.client.model;

// 장바구니에 담긴 메뉴와 수량을 관리하는 객체
public class CartItem {

    // Field
    private final MenuItem menuItem;
    private int quantity;

    // AllArgsConstructor
    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    // Getter
    public MenuItem menuItem() {
        return menuItem;
    }

    public int quantity() {
        return quantity;
    }

    // 수량을 1 증가시키는 메서드
    public void increaseQuantity() {
        quantity++;
    }

    // 수량을 1 감소시키는 메서드
    // 수량이 0보다 클때만 감소하도록 처리
    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    // 메뉴 금액과 수량을 곱해서 총 금액을 반환하는 메서드
    public int lineTotal() {
        return menuItem.price() * quantity;
    }
}
