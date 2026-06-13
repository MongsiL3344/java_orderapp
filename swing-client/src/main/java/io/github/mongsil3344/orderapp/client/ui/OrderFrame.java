package io.github.mongsil3344.orderapp.client.ui;

import io.github.mongsil3344.orderapp.client.api.MenuApiClient;
import io.github.mongsil3344.orderapp.client.api.OrderApiClient;
import io.github.mongsil3344.orderapp.client.api.OrderApiResponse;
import io.github.mongsil3344.orderapp.client.model.CartItem;
import io.github.mongsil3344.orderapp.client.model.MenuItem;
import io.github.mongsil3344.orderapp.client.model.OrderItem;
import io.github.mongsil3344.orderapp.client.model.OrderRequest;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class OrderFrame extends JFrame {

    private static final String DEFAULT_API_URL = "http://localhost:8080";

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);
    private final MenuApiClient menuApiClient = new MenuApiClient();
    private final OrderApiClient orderApiClient = new OrderApiClient();
    private final List<CartItem> cartItems = new ArrayList<>();

    private final DefaultListModel<MenuItem> menuListModel = new DefaultListModel<>();
    private final JList<MenuItem> menuList = new EmptyMessageMenuList(menuListModel);
    private final CartTableModel cartTableModel = new CartTableModel(cartItems);
    private final JTable cartTable = new JTable(cartTableModel);
    private final JTextField customerNameField = new JTextField();
    private final JTextField phoneNumberField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField apiUrlField = new JTextField(DEFAULT_API_URL);
    private final JLabel totalPriceLabel = new JLabel("총 결제 금액: 0원");
    private final JLabel statusLabel = new JLabel("백엔드를 실행한 뒤 주문을 전송하세요.");
    private final JButton homeButton = new JButton("처음으로");
    private final JButton addButton = new JButton("담기");
    private final JButton reloadMenuButton = new JButton("새로고침");
    private final JButton increaseButton = new JButton("+");
    private final JButton decreaseButton = new JButton("-");
    private final JButton removeButton = new JButton("삭제");
    private final JButton clearButton = new JButton("비우기");
    private final JButton orderButton = new JButton("주문하기");

    public OrderFrame() {
        setTitle("배달 주문 프로그램");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 620));
        setLocationByPlatform(true);

        setLayout(new BorderLayout(16, 16));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.WEST);
        add(createOrderPanel(), BorderLayout.CENTER);

        bindActions();
        updateTotalPrice();
        pack();
        setLocationRelativeTo(null);
        loadMenusFromBackend();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        homeButton.setPreferredSize(new Dimension(104, 34));
        panel.add(homeButton, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(300, 520));

        JLabel titleLabel = new JLabel("메뉴");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(titleLabel, BorderLayout.NORTH);

        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFixedCellHeight(68);
        menuList.setCellRenderer(new MenuCellRenderer());
        panel.add(new JScrollPane(menuList), BorderLayout.CENTER);

        addButton.setPreferredSize(new Dimension(120, 36));
        reloadMenuButton.setPreferredSize(new Dimension(100, 36));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.add(reloadMenuButton);
        buttonPanel.add(addButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.add(createCustomerPanel(), BorderLayout.NORTH);
        panel.add(createCartPanel(), BorderLayout.CENTER);
        panel.add(createOrderButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("주문자 정보"));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(panel, constraints, 0, "이름", customerNameField);
        addFormRow(panel, constraints, 1, "전화번호", phoneNumberField);
        addFormRow(panel, constraints, 2, "주소", addressField);
        addFormRow(panel, constraints, 3, "서버 URL", apiUrlField);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints constraints, int row, String label, JTextField field) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("장바구니"));

        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowHeight(30);
        cartTable.setFillsViewportHeight(true);
        configureCartTableColumns();
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        increaseButton.setPreferredSize(new Dimension(46, 32));
        decreaseButton.setPreferredSize(new Dimension(46, 32));
        removeButton.setPreferredSize(new Dimension(76, 32));
        clearButton.setPreferredSize(new Dimension(76, 32));
        toolsPanel.add(increaseButton);
        toolsPanel.add(decreaseButton);
        toolsPanel.add(removeButton);
        toolsPanel.add(clearButton);

        totalPriceLabel.setFont(totalPriceLabel.getFont().deriveFont(Font.BOLD, 16f));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(toolsPanel, BorderLayout.WEST);
        bottomPanel.add(totalPriceLabel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        orderButton.setPreferredSize(new Dimension(128, 40));
        panel.add(orderButton);
        return panel;
    }

    private void configureCartTableColumns() {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Integer number) {
                    setText(currencyFormat.format(number));
                } else {
                    super.setValue(value);
                }
            }
        };
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel columns = cartTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(220);
        columns.getColumn(1).setPreferredWidth(70);
        columns.getColumn(1).setCellRenderer(centerRenderer);
        columns.getColumn(2).setPreferredWidth(100);
        columns.getColumn(2).setCellRenderer(rightRenderer);
        columns.getColumn(3).setPreferredWidth(100);
        columns.getColumn(3).setCellRenderer(rightRenderer);
    }

    private void bindActions() {
        homeButton.addActionListener(event -> returnToRoleSelection());
        addButton.addActionListener(event -> addSelectedMenuToCart());
        reloadMenuButton.addActionListener(event -> loadMenusFromBackend());
        increaseButton.addActionListener(event -> changeSelectedCartQuantity(1));
        decreaseButton.addActionListener(event -> changeSelectedCartQuantity(-1));
        removeButton.addActionListener(event -> removeSelectedCartItem());
        clearButton.addActionListener(event -> clearCart());
        orderButton.addActionListener(event -> submitOrder());
    }

    private void returnToRoleSelection() {
        RoleSelectionFrame roleSelectionFrame = new RoleSelectionFrame();
        roleSelectionFrame.setVisible(true);
        dispose();
    }

    private void addSelectedMenuToCart() {
        MenuItem selectedMenu = menuList.getSelectedValue();
        if (selectedMenu == null) {
            if (menuListModel.getSize() == 0) {
                showMessage(
                        "등록된 메뉴가 없습니다.\n사장님 화면에서 메뉴를 먼저 등록해주세요.",
                        "메뉴 없음",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            showMessage("메뉴를 선택해주세요.", "메뉴 선택 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.menuItem().name().equals(selectedMenu.name()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity();
        } else {
            cartItems.add(new CartItem(selectedMenu, 1));
        }

        refreshCart();
        statusLabel.setText(selectedMenu.name() + " 메뉴를 장바구니에 담았습니다.");
    }

    private void changeSelectedCartQuantity(int delta) {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("수량을 변경할 메뉴를 장바구니에서 선택해주세요.", "선택 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CartItem selectedItem = cartItems.get(selectedRow);
        if (delta > 0) {
            selectedItem.increaseQuantity();
        } else {
            selectedItem.decreaseQuantity();
            if (selectedItem.quantity() == 0) {
                cartItems.remove(selectedRow);
            }
        }

        refreshCart();
    }

    private void removeSelectedCartItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("삭제할 메뉴를 장바구니에서 선택해주세요.", "선택 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cartItems.remove(selectedRow);
        refreshCart();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            return;
        }

        cartItems.clear();
        refreshCart();
        statusLabel.setText("장바구니를 비웠습니다.");
    }

    private void submitOrder() {
        String customerName = customerNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String address = addressField.getText().trim();

        if (customerName.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
            showMessage("이름, 전화번호, 주소를 모두 입력해주세요.", "입력 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cartItems.isEmpty()) {
            showMessage("장바구니에 메뉴를 먼저 담아주세요.", "장바구니 비어 있음", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrderRequest orderRequest = new OrderRequest(
                customerName,
                phoneNumber,
                address,
                Instant.now().toString(),
                calculateTotalPrice(),
                toOrderItems()
        );

        setOrderingState(true);
        CompletableFuture
                .supplyAsync(() -> sendOrder(orderRequest))
                .whenComplete((response, error) -> SwingUtilities.invokeLater(() -> handleOrderResult(response, error)));
    }

    private OrderApiResponse sendOrder(OrderRequest orderRequest) {
        try {
            return orderApiClient.postOrder(apiUrlField.getText(), orderRequest);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new CompletionException(exception);
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
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

        menuListModel.clear();
        for (MenuItem menu : menus) {
            menuListModel.addElement(menu);
        }

        if (!menus.isEmpty()) {
            menuList.setSelectedIndex(0);
        }
        statusLabel.setText("DB에서 메뉴 " + menus.size() + "개를 불러왔습니다.");
    }

    private void handleOrderResult(OrderApiResponse response, Throwable error) {
        setOrderingState(false);

        if (error != null) {
            Throwable cause = unwrapCompletionException(error);
            statusLabel.setText("주문 전송 실패");
            showMessage(
                    "주문 전송에 실패했습니다.\n백엔드 서버가 실행 중인지 확인해주세요.\n\n원인: " + cause.getMessage(),
                    "전송 실패",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (response.isSuccess()) {
            int totalPrice = calculateTotalPrice();
            String orderSummary = formatOrderSummary();
            cartItems.clear();
            refreshCart();
            statusLabel.setText("주문이 백엔드로 전송되었습니다. 응답 코드: " + response.statusCode());
            showMessage(
                    "주문이 완료되었습니다.\n\n주문 내역\n" + orderSummary
                            + "\n\n총 결제 금액: " + currencyFormat.format(totalPrice),
                    "주문 완료",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        statusLabel.setText("백엔드 응답 오류: " + response.statusCode());
        showMessage(
                "백엔드가 오류 응답을 반환했습니다.\n응답 코드: " + response.statusCode()
                        + "\n\n응답 내용:\n" + response.responseBody(),
                "주문 실패",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void setOrderingState(boolean ordering) {
        orderButton.setEnabled(!ordering);
        addButton.setEnabled(!ordering);
        increaseButton.setEnabled(!ordering);
        decreaseButton.setEnabled(!ordering);
        removeButton.setEnabled(!ordering);
        clearButton.setEnabled(!ordering);
        statusLabel.setText(ordering ? "주문서를 백엔드로 전송 중입니다..." : statusLabel.getText());
    }

    private void setMenuLoadingState(boolean loading) {
        addButton.setEnabled(!loading);
        reloadMenuButton.setEnabled(!loading);
        menuList.setEnabled(!loading);
        statusLabel.setText(loading ? "DB에서 메뉴를 불러오는 중입니다..." : statusLabel.getText());
    }

    private Throwable unwrapCompletionException(Throwable error) {
        if (error instanceof CompletionException && error.getCause() != null) {
            return error.getCause();
        }
        return error;
    }

    private List<OrderItem> toOrderItems() {
        return cartItems.stream()
                .map(item -> new OrderItem(item.menuItem().name(), item.menuItem().price(), item.quantity()))
                .toList();
    }

    private void refreshCart() {
        cartTableModel.refresh();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPriceLabel.setText("총 결제 금액: " + currencyFormat.format(calculateTotalPrice()));
    }

    private int calculateTotalPrice() {
        return cartItems.stream()
                .mapToInt(CartItem::lineTotal)
                .sum();
    }

    private String formatOrderSummary() {
        StringBuilder summary = new StringBuilder();
        for (CartItem item : cartItems) {
            if (summary.length() > 0) {
                summary.append("\n");
            }
            summary.append(item.menuItem().name())
                    .append(": ")
                    .append(item.quantity())
                    .append("개");
        }
        return summary.toString();
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private class MenuCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof MenuItem menuItem) {
                label.setText("<html><b>" + escapeHtml(menuItem.name()) + "</b><br>"
                        + "<span style='color:#666666;'>"
                        + currencyFormat.format(menuItem.price())
                        + " · "
                        + escapeHtml(menuItem.description())
                        + "</span></html>");
            }
            label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            return label;
        }

        private String escapeHtml(String text) {
            return text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }

    private static class EmptyMessageMenuList extends JList<MenuItem> {

        EmptyMessageMenuList(DefaultListModel<MenuItem> model) {
            super(model);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getModel().getSize() == 0) {
                EmptyStatePainter.paintCenteredMessage(graphics, this, "메뉴가 없어요");
            }
        }
    }
}
