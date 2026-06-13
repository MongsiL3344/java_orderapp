package io.github.mongsil3344.orderapp.client;

import io.github.mongsil3344.orderapp.client.ui.RoleSelectionFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// 스윙 클라이언트 애플리케이션의 시작점
public class OrderAppClientApplication {

    // main 메서드
    // 스윙 화면은 이벤트 디스패치 스레드에서 실행되도록 invokeLater 사용
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            // 첫 화면인 역할 선택 화면 객체 생성
            RoleSelectionFrame frame = new RoleSelectionFrame();
            frame.setVisible(true);
        });
    }
}
