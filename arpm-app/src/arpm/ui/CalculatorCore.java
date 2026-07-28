package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * The reusable "guts" of the job cost calculator: a base rate field plus a
 * dynamic list of properties (width, length, sealer). Used by both the
 * standalone Job Calculator screen and the calculator dialog launched from
 * the Add/Edit Client form.
 */
public class CalculatorCore extends JPanel {

    private final JTextField baseRateField = new JTextField("0.15");
    private final JPanel propertiesContainer = new JPanel();
    private final List<PropertyRow> rows = new ArrayList<>();
    private final JLabel countLabel = new JLabel();

    public CalculatorCore() {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel rateCard = new JPanel(new BorderLayout(0, 8));
        rateCard.setOpaque(true);
        rateCard.setBackground(Theme.NAVY_LIGHT);
        rateCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel rateLabel = new JLabel("BASE RATE ($ PER SQ FT)");
        rateLabel.setForeground(Theme.GOLD);
        rateLabel.setFont(Theme.FONT_BOLD);

        baseRateField.setColumns(8);
        JPanel rateFieldRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        rateFieldRow.setOpaque(false);
        rateFieldRow.add(baseRateField);

        JLabel rateHint = new JLabel("The rate you charge per square foot before sealer.");
        rateHint.setForeground(Theme.TEXT_MUTED);
        rateHint.setFont(Theme.FONT_BODY);

        JPanel rateTop = new JPanel(new BorderLayout());
        rateTop.setOpaque(false);
        rateTop.add(rateLabel, BorderLayout.NORTH);
        rateTop.add(rateFieldRow, BorderLayout.CENTER);

        rateCard.add(rateTop, BorderLayout.NORTH);
        rateCard.add(rateHint, BorderLayout.SOUTH);

        JPanel propsHeader = new JPanel(new BorderLayout());
        propsHeader.setOpaque(false);
        countLabel.setForeground(Theme.TEXT_LIGHT);
        countLabel.setFont(Theme.FONT_HEADER);

        RoundedButton addPropertyButton = new RoundedButton("+ ADD PROPERTY", Theme.GOLD, Theme.NAVY_DARK);
        addPropertyButton.addActionListener(e -> addProperty());

        propsHeader.add(countLabel, BorderLayout.WEST);
        propsHeader.add(addPropertyButton, BorderLayout.EAST);

        propertiesContainer.setLayout(new BoxLayout(propertiesContainer, BoxLayout.Y_AXIS));
        propertiesContainer.setOpaque(false);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(rateCard);
        top.add(Box.createVerticalStrut(20));
        top.add(propsHeader);
        top.add(Box.createVerticalStrut(10));
        top.add(propertiesContainer);

        JScrollPane scroll = new JScrollPane(top);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        addProperty();
    }

    private void addProperty() {
        final PropertyRow[] holder = new PropertyRow[1];
        PropertyRow row = new PropertyRow(rows.size(), () -> removeProperty(holder[0]));
        holder[0] = row;
        rows.add(row);
        propertiesContainer.add(row);
        propertiesContainer.add(Box.createVerticalStrut(14));
        refreshAfterChange();
    }

    private void removeProperty(PropertyRow row) {
        if (rows.size() <= 1) {
            JOptionPane.showMessageDialog(this, "At least one property is required.",
                    "Cannot Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idx = rows.indexOf(row);
        if (idx < 0) {
            return;
        }
        rows.remove(idx);
        propertiesContainer.removeAll();
        for (PropertyRow r : rows) {
            propertiesContainer.add(r);
            propertiesContainer.add(Box.createVerticalStrut(14));
        }
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setDisplayIndex(i);
        }
        refreshAfterChange();
    }

    private void refreshAfterChange() {
        countLabel.setText("PROPERTIES (" + rows.size() + ")");
        propertiesContainer.revalidate();
        propertiesContainer.repaint();
    }

    /**
     * Computes the total job cost across every property.
     * Throws NumberFormatException if the base rate or any property's
     * width/length is missing or invalid.
     */
    public double calculateTotal() {
        String rateText = baseRateField.getText().trim();
        double baseRate = Double.parseDouble(rateText);
        if (baseRate < 0) {
            throw new NumberFormatException("Base rate cannot be negative.");
        }
        double total = 0;
        for (PropertyRow row : rows) {
            double area = row.area();
            double rate = baseRate + (row.isSealerApplied() ? 1.5 : 0);
            total += area * rate;
        }
        return total;
    }
}
