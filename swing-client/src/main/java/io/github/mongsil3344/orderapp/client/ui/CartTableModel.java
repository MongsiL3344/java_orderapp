package io.github.mongsil3344.orderapp.client.ui;

import io.github.mongsil3344.orderapp.client.model.CartItem;

import javax.swing.table.AbstractTableModel;
import java.util.List;

// 손님 주문창 장바구니 테이블에 사용할 데이터 모델 클래스
public class CartTableModel extends AbstractTableModel {

    // 테이블 컬럼명
    private static final String[] COLUMNS = {"메뉴", "수량", "단가", "금액"};

    // 장바구니 목록을 저장하는 List 자료구조
    private final List<CartItem> cartItems;

    // 생성자
    // 장바구니 목록 객체를 주입받음
    public CartTableModel(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    // 테이블의 행 개수를 반환하는 메서드
    @Override
    public int getRowCount() {
        return cartItems.size();
    }

    // 테이블의 컬럼 개수를 반환하는 메서드
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    // 컬럼명을 반환하는 메서드
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    // 컬럼별 데이터 타입을 반환하는 메서드
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? String.class : Integer.class;
    }

    // row와 column에 해당하는 값을 반환하는 메서드
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CartItem item = cartItems.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.menuItem().name();
            case 1 -> item.quantity();
            case 2 -> item.menuItem().price();
            case 3 -> item.lineTotal();
            default -> "";
        };
    }

    // 장바구니 데이터가 변경됐음을 테이블에 알려주는 메서드
    public void refresh() {
        fireTableDataChanged();
    }
}
