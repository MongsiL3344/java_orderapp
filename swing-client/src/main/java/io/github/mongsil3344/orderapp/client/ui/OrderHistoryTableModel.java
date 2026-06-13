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

// 사장님 주문 내역 테이블에 사용할 데이터 모델 클래스
public class OrderHistoryTableModel extends AbstractTableModel {

    // 테이블 컬럼명
    private static final String[] COLUMNS = {"주문번호", "주문자", "총액", "접수시각"};
    // 서버에서 받은 시간을 한국시간 문자열로 바꾸기 위한 포매터
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            .withZone(ZoneId.of("Asia/Seoul"));

    // 주문 내역 목록을 저장하는 List 자료구조
    private final List<OrderHistoryItem> orders = new ArrayList<>();

    // 테이블의 행 개수를 반환하는 메서드
    @Override
    public int getRowCount() {
        return orders.size();
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
        OrderHistoryItem order = orders.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> order.id();
            case 1 -> order.customerName();
            case 2 -> order.totalPrice();
            case 3 -> formatCreatedAt(order.createdAt());
            default -> "";
        };
    }

    // 주문 목록을 새로 저장하고 테이블 갱신을 알리는 메서드
    public void setOrders(List<OrderHistoryItem> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        fireTableDataChanged();
    }

    // 선택한 행의 OrderHistoryItem 객체를 반환하는 메서드
    public OrderHistoryItem getOrderAt(int rowIndex) {
        return orders.get(rowIndex);
    }

    // 서버에서 받은 접수시각 문자열을 화면 표시용으로 변환하는 메서드
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
