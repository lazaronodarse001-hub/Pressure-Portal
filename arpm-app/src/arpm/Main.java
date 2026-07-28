package arpm;

import arpm.store.ClientStore;
import arpm.ui.ClientProfilesPanel;
import arpm.ui.JobCalculatorPanel;
import arpm.ui.MainMenuPanel;
import arpm.ui.ScheduleCalendarPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Entry point for the A&R Property Maintenance Employee Management System.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGui);
    }

    private static void createAndShowGui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }

        JFrame frame = new JFrame("A&R Property Maintenance \u2014 Employee System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 650));
        frame.setSize(1150, 720);
        frame.setLocationRelativeTo(null);

        ClientStore store = new ClientStore();

        CardLayout cardLayout = new CardLayout();
        JPanel cards = new JPanel(cardLayout);

        Runnable goHome = () -> cardLayout.show(cards, "menu");

        MainMenuPanel menuPanel = new MainMenuPanel(
                () -> cardLayout.show(cards, "profiles"),
                () -> cardLayout.show(cards, "calculator"),
                () -> cardLayout.show(cards, "calendar")
        );

        ClientProfilesPanel profilesPanel = new ClientProfilesPanel(store, goHome);
        JobCalculatorPanel calculatorPanel = new JobCalculatorPanel(goHome);
        ScheduleCalendarPanel calendarPanel = new ScheduleCalendarPanel(store, goHome);

        cards.add(menuPanel, "menu");
        cards.add(profilesPanel, "profiles");
        cards.add(calculatorPanel, "calculator");
        cards.add(calendarPanel, "calendar");

        frame.setContentPane(cards);
        frame.setVisible(true);
    }
}
