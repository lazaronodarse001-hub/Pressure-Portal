package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

/**
 * Modal dialog wrapping {@link CalculatorCore}, used when creating or
 * editing a client so the job total can be computed on the spot.
 */
public class CalculatorDialog extends JDialog {

    private Double result;
    private final CalculatorCore core = new CalculatorCore();

    private CalculatorDialog(Frame owner) {
        super(owner, "Job Cost Calculator", true);
        setSize(540, 580);
        setLocationRelativeTo(owner);

        GradientPanel content = new GradientPanel(Theme.NAVY, Theme.NAVY_DARK);
        content.setLayout(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(content);

        content.add(core, BorderLayout.CENTER);

        JLabel resultLabel = new JLabel(" ");
        resultLabel.setForeground(Theme.GOLD);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        RoundedButton calculateButton = new RoundedButton("CALCULATE TOTAL", Theme.GOLD, Theme.NAVY_DARK);
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actionRow.setOpaque(false);

        RoundedButton useTotalButton = new RoundedButton("USE THIS TOTAL", Theme.GOLD, Theme.NAVY_DARK);
        RoundedButton cancelButton = new RoundedButton("CANCEL", Theme.NAVY_LIGHT, Theme.TEXT_LIGHT);
        useTotalButton.setEnabled(false);

        calculateButton.addActionListener(e -> {
            try {
                double total = core.calculateTotal();
                resultLabel.setText(String.format("Estimated Total: $%.2f", total));
                useTotalButton.setEnabled(true);
                result = total;
            } catch (NumberFormatException ex) {
                resultLabel.setText(" ");
                useTotalButton.setEnabled(false);
                result = null;
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for the base rate and every property's width/length.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        useTotalButton.addActionListener(e -> dispose());
        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });

        actionRow.add(cancelButton);
        actionRow.add(useTotalButton);

        bottom.add(calculateButton);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(resultLabel);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(actionRow);

        content.add(bottom, BorderLayout.SOUTH);
    }

    /**
     * Shows the calculator dialog and returns the total the user chose to
     * use, or {@code null} if they cancelled without confirming a total.
     */
    public static Double showDialog(Frame owner) {
        CalculatorDialog dialog = new CalculatorDialog(owner);
        dialog.setVisible(true);
        return dialog.result;
    }
}
