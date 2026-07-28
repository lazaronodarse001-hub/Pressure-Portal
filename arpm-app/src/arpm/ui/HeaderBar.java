package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * The gold header bar with a back button and screen title, used on every
 * screen except the main menu.
 */
public class HeaderBar extends JPanel {

    public HeaderBar(String title, Runnable onBack) {
        setLayout(new BorderLayout());
        setBackground(Theme.GOLD);
        setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        RoundedButton backButton = new RoundedButton("\u2190 BACK", Theme.NAVY_DARK, Theme.GOLD);
        backButton.addActionListener(e -> onBack.run());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.NAVY_DARK);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);
        left.add(backButton);
        left.add(titleLabel);

        add(left, BorderLayout.WEST);
    }
}
