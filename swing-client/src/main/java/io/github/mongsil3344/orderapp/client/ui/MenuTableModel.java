package io.github.mongsil3344.orderapp.client.ui;

import io.github.mongsil3344.orderapp.client.model.MenuItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MenuTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"번호", "메뉴", "가격", "설명"};

    private final List<MenuItem> menus = new ArrayList<>();

    @Override
    public int getRowCount() {
        return menus.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Long.class;
            case 2 -> Integer.class;
            default -> String.class;
        };
    }

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

    public void setMenus(List<MenuItem> newMenus) {
        menus.clear();
        menus.addAll(newMenus);
        fireTableDataChanged();
    }

    public MenuItem getMenuAt(int rowIndex) {
        return menus.get(rowIndex);
    }
}
