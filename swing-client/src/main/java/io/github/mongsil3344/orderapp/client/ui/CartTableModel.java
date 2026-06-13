package io.github.mongsil3344.orderapp.client.ui;

import io.github.mongsil3344.orderapp.client.model.CartItem;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CartTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"메뉴", "수량", "단가", "금액"};

    private final List<CartItem> cartItems;

    public CartTableModel(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public int getRowCount() {
        return cartItems.size();
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
        return columnIndex == 0 ? String.class : Integer.class;
    }

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

    public void refresh() {
        fireTableDataChanged();
    }
}
