package io.github.mongsil3344.orderapp.client;

import io.github.mongsil3344.orderapp.client.ui.RoleSelectionFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class OrderAppClientApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            RoleSelectionFrame frame = new RoleSelectionFrame();
            frame.setVisible(true);
        });
    }
}
