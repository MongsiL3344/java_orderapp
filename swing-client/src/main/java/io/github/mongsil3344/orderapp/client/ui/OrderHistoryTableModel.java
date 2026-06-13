package io.github.mongsil3344.orderapp.client.ui;

import io.github.mongsil3344.orderapp.client.model.OrderHistoryItem;

import javax.swing.table.AbstractTableModel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"주문번호", "주문자", "총액", "접수시각"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            .withZone(ZoneId.of("Asia/Seoul"));

    private final List<OrderHistoryItem> orders = new ArrayList<>();

    @Override
    public int getRowCount() {
        return orders.size();
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
        OrderHistoryItem order = orders.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> order.id();
            case 1 -> order.customerName();
            case 2 -> order.totalPrice();
            case 3 -> formatCreatedAt(order.createdAt());
            default -> "";
        };
    }

    public void setOrders(List<OrderHistoryItem> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        fireTableDataChanged();
    }

    public OrderHistoryItem getOrderAt(int rowIndex) {
        return orders.get(rowIndex);
    }

    private String formatCreatedAt(String createdAt) {
        if (createdAt == null || createdAt.isBlank()) {
            return "";
        }

        try {
            return DATE_TIME_FORMATTER.format(Instant.parse(createdAt));
        } catch (DateTimeParseException exception) {
            return createdAt;
        }
    }
}
