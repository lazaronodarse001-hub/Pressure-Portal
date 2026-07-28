package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * The landing screen shown when the app starts, with the three main
 * navigation options.
 */
public class MainMenuPanel extends JPanel {

    public MainMenuPanel(Runnable onProfiles, Runnable onCalculator, Runnable onCalendar) {
        setLayout(new BorderLayout());

        // Top banner (company name - no phone number, this is an internal employee tool)
        GradientPanel banner = new GradientPanel(Theme.GOLD.brighter(), Theme.GOLD_DARK);
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBorder(BorderFactory.createEmptyBorder(34, 20, 34, 20));

        JLabel company = new JLabel("A & R PROPERTY MAINTENANCE");
        company.setFont(new Font("Serif", Font.BOLD, 30));
        company.setForeground(Theme.NAVY_DARK);
        company.setAlignmentX(Component.CENTER_ALIGNMENT);
        company.setHorizontalAlignment(JLabel.CENTER);

        JLabel sub = new JLabel("PRESSURE WASHING");
        sub.setFont(new Font("SansSerif", Font.BOLD, 16));
        sub.setForeground(Theme.NAVY_DARK);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setHorizontalAlignment(JLabel.CENTER);

        banner.add(company);
        banner.add(Box.createVerticalStrut(6));
        banner.add(sub);

        // Main content area
        GradientPanel content = new GradientPanel(Theme.NAVY, Theme.NAVY_DARK);
        content.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(40, 160, 20, 160));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel heading = new JLabel("EMPLOYEE MANAGEMENT SYSTEM");
        heading.setFont(Theme.FONT_HEADER);
        heading.setForeground(Theme.TEXT_LIGHT);
        heading.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        body.add(heading, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        body.add(new MenuCard("\uD83D\uDC64", "CLIENT PROFILES", "Create & manage client records", onProfiles), gbc);

        gbc.gridy = 2;
        body.add(new MenuCard("\uD83E\uDDEE", "JOB CALCULATOR", "Calculate job pricing by sq ft", onCalculator), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(new MenuCard("\uD83D\uDCC5", "SCHEDULE CALENDAR", "Assign & manage job dates", onCalendar), gbc);

        gbc.gridy = 4;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        body.add(Box.createGlue(), gbc);

        JLabel footer = new JLabel("\u00A9 A&R Property Maintenance \u2014 Employee Use Only");
        footer.setForeground(Theme.TEXT_MUTED);
        footer.setHorizontalAlignment(JLabel.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        content.add(body, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);

        add(banner, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
