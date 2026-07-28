package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * A JButton painted as a solid rounded rectangle, styled to match the
 * gold/navy look used throughout the app.
 */
public class RoundedButton extends JButton {

    private final Color bgColor;
    private final Color hoverColor;
    private final int arc = 16;

    public RoundedButton(String text, Color background, Color foreground) {
        super(text);
        this.bgColor = background;
        this.hoverColor = background.brighter();
        setForeground(foreground);
        setFont(Theme.FONT_BOLD);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = bgColor;
        if (!isEnabled()) {
            fill = Color.GRAY;
        } else if (getModel().isPressed()) {
            fill = bgColor.darker();
        } else if (getModel().isRollover()) {
            fill = hoverColor;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
