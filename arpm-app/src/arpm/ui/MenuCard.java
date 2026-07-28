package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A large clickable gold card shown on the main menu (Client Profiles,
 * Job Calculator, Schedule Calendar).
 */
public class MenuCard extends JPanel {

    private boolean hover = false;

    public MenuCard(String icon, String title, String subtitle, Runnable onClick) {
        setOpaque(false);
        setLayout(new BorderLayout(20, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));
        setPreferredSize(new Dimension(600, 92));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 34));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_HEADER);
        titleLabel.setForeground(Theme.NAVY_DARK);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(new Color(0x33, 0x3A, 0x5C));

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color top = hover ? Theme.GOLD.brighter() : Theme.GOLD;
        GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), Theme.GOLD_DARK);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, 92);
    }
}
