package io.github.mongsil3344.orderapp.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

final class EmptyStatePainter {

    private static final Color EMPTY_MESSAGE_COLOR = new Color(150, 150, 150);

    private EmptyStatePainter() {
    }

    static void paintCenteredMessage(Graphics graphics, Component component, String message) {
        Font originalFont = graphics.getFont();
        Font messageFont = originalFont.deriveFont(Font.BOLD, 16f);
        FontMetrics metrics = graphics.getFontMetrics(messageFont);

        int x = Math.max(0, (component.getWidth() - metrics.stringWidth(message)) / 2);
        int y = Math.max(
                metrics.getAscent(),
                (component.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent()
        );

        graphics.setFont(messageFont);
        graphics.setColor(EMPTY_MESSAGE_COLOR);
        graphics.drawString(message, x, y);
        graphics.setFont(originalFont);
    }
}
