package view;

import javax.swing.*;
import java.awt.*;

public class CircleButton extends JButton {

    public CircleButton(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Khử răng cưa
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nền tròn
        g2.setColor(new Color(255, 255, 255, 180)); // nền trắng mờ
        g2.fillOval(0, 0, getWidth(), getHeight());

        // Viền
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean contains(int x, int y) {
        int radius = getWidth() / 2;
        int centerX = radius;
        int centerY = radius;
        return (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= Math.pow(radius, 2);
    }
    @Override
    public Insets getInsets() {
        return new Insets(5, 5, 5, 5);  // đẩy nội dung lên trên
    }

}
