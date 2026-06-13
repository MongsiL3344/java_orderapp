package io.github.mongsil3344.orderapp.client.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

// 손님, 사장님 역할을 선택하는 첫 화면 클래스
public class RoleSelectionFrame extends JFrame {

    // 생성자
    // 역할 선택 화면에 필요한 컴포넌트들을 생성하고 배치함
    public RoleSelectionFrame() {
        setTitle("배달 주문 앱");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(460, 260));
        setLocationByPlatform(true);

        JPanel rootPanel = new JPanel(new BorderLayout(16, 24));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        setContentPane(rootPanel);

        JLabel titleLabel = new JLabel("배달 주문 앱", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        rootPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        JButton customerButton = new JButton("손님이에요");
        JButton ownerButton = new JButton("사장님이에요");
        customerButton.setPreferredSize(new Dimension(160, 56));
        ownerButton.setPreferredSize(new Dimension(160, 56));
        customerButton.setFont(customerButton.getFont().deriveFont(Font.BOLD, 16f));
        ownerButton.setFont(ownerButton.getFont().deriveFont(Font.BOLD, 16f));

        customerButton.addActionListener(event -> openFrame(new OrderFrame()));
        ownerButton.addActionListener(event -> openFrame(new OwnerFrame()));

        buttonPanel.add(customerButton);
        buttonPanel.add(ownerButton);
        rootPanel.add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    // 선택한 화면을 열고 현재 역할 선택 화면은 닫는 메서드
    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }
}
