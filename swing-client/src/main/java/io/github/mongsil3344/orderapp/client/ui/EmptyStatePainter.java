package io.github.mongsil3344.orderapp.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

// 데이터가 없을 때 빈 화면 문구를 그리는 유틸 클래스
final class EmptyStatePainter {

    // 빈 화면 문구 색상
    private static final Color EMPTY_MESSAGE_COLOR = new Color(150, 150, 150);

    // 객체 생성을 막기 위한 private 생성자
    private EmptyStatePainter() {
    }

    // 컴포넌트 가운데에 문구를 그리는 메서드
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
