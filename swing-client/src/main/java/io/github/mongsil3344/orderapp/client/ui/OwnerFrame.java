package io.github.mongsil3344.orderapp.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mongsil3344.orderapp.client.api.MenuApiClient;
import io.github.mongsil3344.orderapp.client.api.OrderApiClient;
import io.github.mongsil3344.orderapp.client.model.MenuItem;
import io.github.mongsil3344.orderapp.client.model.OrderHistoryItem;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class OwnerFrame extends JFrame {

    private static final String DEFAULT_API_URL = "http://localhost:8080";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            .withZone(ZoneId.of("Asia/Seoul"));

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MenuApiClient menuApiClient = new MenuApiClient();
    private final OrderApiClient orderApiClient = new OrderApiClient();

    private final JTextField apiUrlField = new JTextField(DEFAULT_API_URL);
    private final JLabel statusLabel = new JLabel("백엔드를 실행한 뒤 데이터를 새로고침하세요.");
    private final JButton homeButton = new JButton("처음으로");

    private final MenuTableModel menuTableModel = new MenuTableModel();
    private final JTable menuTable = new EmptyMessageTable(menuTableModel);
    private final JButton reloadMenuButton = new JButton("새로고침");
    private final JButton addMenuButton = new JButton("추가");
    private final JButton editMenuButton = new JButton("수정");
    private final JButton deleteMenuButton = new JButton("삭제");

    private final OrderHistoryTableModel orderHistoryTableModel = new OrderHistoryTableModel();
    private final JTable orderTable = new JTable(orderHistoryTableModel);
    private final JTextArea orderDetailArea = new JTextArea();
    private final JButton reloadOrdersButton = new JButton("새로고침");
    private final JButton reloadAllButton = new JButton("전체 새로고침");

    public OwnerFrame() {
        setTitle("매장 관리");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setLocationByPlatform(true);

        setLayout(new BorderLayout(16, 16));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(createServerPanel(), BorderLayout.NORTH);
        add(createTabbedPane(), BorderLayout.CENTER);

        bindActions();
        pack();
        setLocationRelativeTo(null);
        loadMenusFromBackend();
        loadOrdersFromBackend();
    }

    private JPanel createServerPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        homeButton.setPreferredSize(new Dimension(104, 34));
        panel.add(homeButton, BorderLayout.WEST);

        JPanel serverUrlPanel = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel("서버 URL");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        serverUrlPanel.add(label, BorderLayout.WEST);
        serverUrlPanel.add(apiUrlField, BorderLayout.CENTER);
        panel.add(serverUrlPanel, BorderLayout.CENTER);

        reloadAllButton.setPreferredSize(new Dimension(128, 34));
        panel.add(reloadAllButton, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("메뉴 관리", createMenuPanel());
        tabbedPane.addTab("주문 내역", createOrderPanel());
        return tabbedPane;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.setAutoCreateRowSorter(true);
        menuTable.setRowHeight(30);
        menuTable.setFillsViewportHeight(true);
        configureMenuTableColumns();
        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        reloadMenuButton.setPreferredSize(new Dimension(100, 34));
        addMenuButton.setPreferredSize(new Dimension(80, 34));
        editMenuButton.setPreferredSize(new Dimension(80, 34));
        deleteMenuButton.setPreferredSize(new Dimension(80, 34));
        buttonPanel.add(reloadMenuButton);
        buttonPanel.add(addMenuButton);
        buttonPanel.add(editMenuButton);
        buttonPanel.add(deleteMenuButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setAutoCreateRowSorter(true);
        orderTable.setRowHeight(30);
        orderTable.setFillsViewportHeight(true);
        configureOrderTableColumns();

        orderDetailArea.setEditable(false);
        orderDetailArea.setLineWrap(true);
        orderDetailArea.setWrapStyleWord(true);
        orderDetailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(orderTable),
                new JScrollPane(orderDetailArea)
        );
        splitPane.setResizeWeight(0.55);
        splitPane.setPreferredSize(new Dimension(900, 520));
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        reloadOrdersButton.setPreferredSize(new Dimension(100, 34));
        buttonPanel.add(reloadOrdersButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void configureMenuTableColumns() {
        DefaultTableCellRenderer rightRenderer = createCurrencyRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel columns = menuTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(70);
        columns.getColumn(0).setCellRenderer(centerRenderer);
        columns.getColumn(1).setPreferredWidth(180);
        columns.getColumn(2).setPreferredWidth(110);
        columns.getColumn(2).setCellRenderer(rightRenderer);
        columns.getColumn(3).setPreferredWidth(420);
    }

    private void configureOrderTableColumns() {
        DefaultTableCellRenderer rightRenderer = createCurrencyRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel columns = orderTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(90);
        columns.getColumn(0).setCellRenderer(centerRenderer);
        columns.getColumn(1).setPreferredWidth(160);
        columns.getColumn(2).setPreferredWidth(130);
        columns.getColumn(2).setCellRenderer(rightRenderer);
        columns.getColumn(3).setPreferredWidth(220);
    }

    private DefaultTableCellRenderer createCurrencyRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Integer number) {
                    setText(currencyFormat.format(number));
                } else {
                    super.setValue(value);
                }
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
    }

    private void bindActions() {
        homeButton.addActionListener(event -> returnToRoleSelection());
        reloadAllButton.addActionListener(event -> {
            loadMenusFromBackend();
            loadOrdersFromBackend();
        });
        reloadMenuButton.addActionListener(event -> loadMenusFromBackend());
        addMenuButton.addActionListener(event -> addMenu());
        editMenuButton.addActionListener(event -> editSelectedMenu());
        deleteMenuButton.addActionListener(event -> deleteSelectedMenu());
        reloadOrdersButton.addActionListener(event -> loadOrdersFromBackend());
        orderTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedOrderDetails();
            }
        });
    }

    private void returnToRoleSelection() {
        RoleSelectionFrame roleSelectionFrame = new RoleSelectionFrame();
        roleSelectionFrame.setVisible(true);
        dispose();
    }

    private void loadMenusFromBackend() {
        setMenuLoadingState(true);
        CompletableFuture
                .supplyAsync(this::fetchMenus)
                .whenComplete((menus, error) -> SwingUtilities.invokeLater(() -> handleMenuResult(menus, error)));
    }

    private List<MenuItem> fetchMenus() {
        try {
            return menuApiClient.fetchMenus(apiUrlField.getText());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CompletionException(exception);
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
    }

    private void handleMenuResult(List<MenuItem> menus, Throwable error) {
        setMenuLoadingState(false);

        if (error != null) {
            Throwable cause = unwrapCompletionException(error);
            statusLabel.setText("메뉴 조회 실패");
            showMessage(
                    "메뉴를 불러오지 못했습니다.\n백엔드 서버가 실행 중인지 확인해주세요.\n\n원인: " + cause.getMessage(),
                    "메뉴 조회 실패",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        menuTableModel.setMenus(menus);
        if (!menus.isEmpty()) {
            menuTable.setRowSelectionInterval(0, 0);
        }
        statusLabel.setText("메뉴 " + menus.size() + "개를 불러왔습니다.");
    }

    private void addMenu() {
        Optional<MenuItem> menuItem = promptMenu(null);
        if (menuItem.isEmpty()) {
            return;
        }

        setMenuLoadingState(true);
        CompletableFuture
                .supplyAsync(() -> createMenu(menuItem.get()))
                .whenComplete((createdMenu, error) -> SwingUtilities.invokeLater(() -> handleMenuMutationResult(error)));
    }

    private MenuItem createMenu(MenuItem menuItem) {
        try {
            return menuApiClient.createMenu(apiUrlField.getText(), menuItem);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CompletionException(exception);
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
    }

    private void editSelectedMenu() {
        Optional<MenuItem> selectedMenu = getSelectedMenu();
        if (selectedMenu.isEmpty()) {
            showMessage("수정할 메뉴를 선택해주세요.", "선택 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<MenuItem> editedMenu = promptMenu(selectedMenu.get());
        if (editedMenu.isEmpty()) {
            return;
        }

        setMenuLoadingState(true);
        CompletableFuture
                .supplyAsync(() -> updateMenu(editedMenu.get()))
                .whenComplete((updatedMenu, error) -> SwingUtilities.invokeLater(() -> handleMenuMutationResult(error)));
    }

    private MenuItem updateMenu(MenuItem menuItem) {
        try {
            return menuApiClient.updateMenu(apiUrlField.getText(), menuItem);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CompletionException(exception);
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
    }

    private void deleteSelectedMenu() {
        Optional<MenuItem> selectedMenu = getSelectedMenu();
        if (selectedMenu.isEmpty()) {
            showMessage("삭제할 메뉴를 선택해주세요.", "선택 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MenuItem menu = selectedMenu.get();
        int result = JOptionPane.showConfirmDialog(
                this,
                "'" + menu.name() + "' 메뉴를 삭제할까요?",
                "메뉴 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        setMenuLoadingState(true);
        CompletableFuture
                .supplyAsync(() -> deleteMenu(menu))
                .whenComplete((ignored, error) -> SwingUtilities.invokeLater(() -> handleMenuMutationResult(error)));
    }

    private Void deleteMenu(MenuItem menuItem) {
        try {
            menuApiClient.deleteMenu(apiUrlField.getText(), menuItem.id());
            return null;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CompletionException(exception);
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
    }

    private void handleMenuMutationResult(Throwable error) {
        setMenuLoadingState(false);

        if (error != null) {
            Throwable cause = unwrapCompletionException(error);
            statusLabel.setText("메뉴 변경 실패");
            showMessage(
                    "메뉴 변경에 실패했습니다.\n\n원인: " + cause.getMessage(),
                    "메뉴 변경 실패",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        statusLabel.setText("메뉴 변경을 저장했습니다.");
        loadMenusFromBackend();
    }

    private Optional<MenuItem> promptMenu(MenuItem existingMenu) {
        JTextField nameField = new JTextField(existingMenu == null ? "" : existingMenu.name(), 22);
        JTextField priceField = new JTextField(existingMenu == null ? "" : String.valueOf(existingMenu.price()), 22);
        JTextArea descriptionArea = new JTextArea(existingMenu == null ? "" : existingMenu.description(), 4, 22);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        addDialogRow(formPanel, constraints, 0, "메뉴명", nameField);
        addDialogRow(formPanel, constraints, 1, "가격", priceField);
        addDialogRow(formPanel, constraints, 2, "설명", new JScrollPane(descriptionArea));

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    formPanel,
                    existingMenu == null ? "메뉴 추가" : "메뉴 수정",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            int price;
            try {
                price = Integer.parseInt(priceField.getText().trim());
            } catch (NumberFormatException exception) {
                showMessage("가격은 숫자로 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (name.isBlank()) {
                showMessage("메뉴명을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (price < 0) {
                showMessage("가격은 0원 이상이어야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (description.isBlank()) {
                showMessage("설명을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            Long id = existingMenu == null ? null : existingMenu.id();
            return Optional.of(new MenuItem(id, name, price, description));
        }
    }

    private void addDialogRow(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String label,
            Component field
    ) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private Optional<MenuItem> getSelectedMenu() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow < 0) {
            return Optional.empty();
        }
        int modelRow = menuTable.convertRowIndexToModel(selectedRow);
        return Optional.of(menuTableModel.getMenuAt(modelRow));
    }

    private void loadOrdersFromBackend() {
        setOrderLoadingState(true);
        CompletableFuture
                .supplyAsync(this::fetchOrders)
                .whenComplete((orders, error) -> SwingUtilities.invokeLater(() -> handleOrderResult(orders, error)));
    }

    private List<OrderHistoryItem> fetchOrders() {
        try {
            return orderApiClient.fetchOrders(apiUrlField.getText());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CompletionException(exception);
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
    }

    private void handleOrderResult(List<OrderHistoryItem> orders, Throwable error) {
        setOrderLoadingState(false);

        if (error != null) {
            Throwable cause = unwrapCompletionException(error);
            statusLabel.setText("주문 조회 실패");
            showMessage(
                    "주문 내역을 불러오지 못했습니다.\n백엔드 서버가 실행 중인지 확인해주세요.\n\n원인: " + cause.getMessage(),
                    "주문 조회 실패",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        orderHistoryTableModel.setOrders(orders);
        if (!orders.isEmpty()) {
            orderTable.setRowSelectionInterval(0, 0);
        } else {
            orderDetailArea.setText("");
        }
        statusLabel.setText("주문 " + orders.size() + "건을 불러왔습니다.");
    }

    private void showSelectedOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            orderDetailArea.setText("");
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        OrderHistoryItem order = orderHistoryTableModel.getOrderAt(modelRow);
        orderDetailArea.setText(formatOrderDetails(order));
        orderDetailArea.setCaretPosition(0);
    }

    private String formatOrderDetails(OrderHistoryItem order) {
        JsonNode orderJson = readOrderJson(order.orderJson());
        return """
                주문번호: %s
                주문자: %s
                전화번호: %s
                주소: %s
                총액: %s
                접수시각: %s

                주문 메뉴
                %s
                """.formatted(
                order.id(),
                getText(orderJson, "customerName", order.customerName()),
                getText(orderJson, "phoneNumber", "-"),
                getText(orderJson, "address", "-"),
                currencyFormat.format(order.totalPrice()),
                formatCreatedAt(order.createdAt()),
                formatOrderItems(orderJson)
        );
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

    private JsonNode readOrderJson(String orderJson) {
        if (orderJson == null || orderJson.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readTree(orderJson);
        } catch (IOException exception) {
            return null;
        }
    }

    private String getText(JsonNode node, String fieldName, String fallback) {
        if (node == null) {
            return fallback;
        }

        String value = node.path(fieldName).asText("");
        return value.isBlank() ? fallback : value;
    }

    private String formatOrderItems(JsonNode orderJson) {
        if (orderJson == null || !orderJson.path("items").isArray() || orderJson.path("items").isEmpty()) {
            return "주문 메뉴 정보가 없습니다.";
        }

        StringBuilder summary = new StringBuilder();
        for (JsonNode itemNode : orderJson.path("items")) {
            if (summary.length() > 0) {
                summary.append("\n");
            }
            summary.append(getText(itemNode, "menuName", "이름 없는 메뉴"))
                    .append(": ")
                    .append(itemNode.path("quantity").asInt(0))
                    .append("개");
        }
        return summary.toString();
    }

    private void setMenuLoadingState(boolean loading) {
        reloadMenuButton.setEnabled(!loading);
        addMenuButton.setEnabled(!loading);
        editMenuButton.setEnabled(!loading);
        deleteMenuButton.setEnabled(!loading);
        menuTable.setEnabled(!loading);
        if (loading) {
            statusLabel.setText("메뉴 데이터를 처리하는 중입니다...");
        }
    }

    private void setOrderLoadingState(boolean loading) {
        reloadOrdersButton.setEnabled(!loading);
        orderTable.setEnabled(!loading);
        if (loading) {
            statusLabel.setText("주문 내역을 불러오는 중입니다...");
        }
    }

    private Throwable unwrapCompletionException(Throwable error) {
        if (error instanceof CompletionException && error.getCause() != null) {
            return error.getCause();
        }
        return error;
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private static class EmptyMessageTable extends JTable {

        EmptyMessageTable(MenuTableModel model) {
            super(model);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getModel().getRowCount() == 0) {
                EmptyStatePainter.paintCenteredMessage(graphics, this, "메뉴가 없어요");
            }
        }
    }
}
