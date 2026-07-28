package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Standalone "Job Calculator" screen reached from the main menu. Lets an
 * employee estimate a job total without creating/saving a client record.
 */
public class JobCalculatorPanel extends JPanel {

    private final CalculatorCore core = new CalculatorCore();
    private final JLabel resultLabel = new JLabel(" ");

    public JobCalculatorPanel(Runnable onBack) {
        setLayout(new BorderLayout());
        add(new HeaderBar("JOB COST CALCULATOR", onBack), BorderLayout.NORTH);

        GradientPanel content = new GradientPanel(Theme.NAVY, Theme.NAVY_DARK);
        content.setLayout(new BorderLayout(0, 16));
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        content.add(core, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        RoundedButton calculateButton = new RoundedButton("CALCULATE TOTAL", Theme.GOLD, Theme.NAVY_DARK);
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        calculateButton.addActionListener(e -> {
            try {
                double total = core.calculateTotal();
                resultLabel.setText(String.format("Estimated Job Total: $%.2f", total));
            } catch (NumberFormatException ex) {
                resultLabel.setText(" ");
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for the base rate and every property's width/length.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        resultLabel.setForeground(Theme.GOLD);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        bottom.add(calculateButton);
        bottom.add(resultLabel);

        content.add(bottom, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
    }
}
