package io.github.mongsil3344.orderapp.client.ui;

import io.github.mongsil3344.orderapp.client.model.MenuItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// 사장님 메뉴 관리 테이블에 사용할 데이터 모델 클래스
public class MenuTableModel extends AbstractTableModel {

    // 테이블 컬럼명
    private static final String[] COLUMNS = {"번호", "메뉴", "가격", "설명"};

    // 메뉴 목록을 저장하는 List 자료구조
    private final List<MenuItem> menus = new ArrayList<>();

    // 테이블의 행 개수를 반환하는 메서드
    @Override
    public int getRowCount() {
        return menus.size();
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
        return switch (columnIndex) {
            case 0 -> Long.class;
            case 2 -> Integer.class;
            default -> String.class;
        };
    }

    // row와 column에 해당하는 값을 반환하는 메서드
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MenuItem menu = menus.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> menu.id();
            case 1 -> menu.name();
            case 2 -> menu.price();
            case 3 -> menu.description();
            default -> "";
        };
    }

    // 메뉴 목록을 새로 저장하고 테이블 갱신을 알리는 메서드
    public void setMenus(List<MenuItem> newMenus) {
        menus.clear();
        menus.addAll(newMenus);
        fireTableDataChanged();
    }

    // 선택한 행의 MenuItem 객체를 반환하는 메서드
    public MenuItem getMenuAt(int rowIndex) {
        return menus.get(rowIndex);
    }
}
