package arpm.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Input row for a single property in the job calculator: width, length,
 * and whether a sealer should be applied to that property.
 */
public class PropertyRow extends JPanel {

    private final JTextField widthField = new JTextField();
    private final JTextField lengthField = new JTextField();
    private final JCheckBox sealerCheck = new JCheckBox("Apply Sealer ( +$1.50/sq ft )");
    private final JLabel titleLabel;

    public PropertyRow(int displayIndex, Runnable onRemove) {
        setOpaque(true);
        setBackground(Theme.NAVY_LIGHT);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD, 1),
                BorderFactory.createEmptyBorder(14, 16, 20, 16)));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel("PROPERTY " + (displayIndex + 1));
        titleLabel.setForeground(Theme.GOLD);
        titleLabel.setFont(Theme.FONT_BOLD);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(titleLabel, gbc);

        RoundedButton removeButton = new RoundedButton("REMOVE", Theme.RED_DARK, Theme.RED_TEXT);
        removeButton.addActionListener(e -> onRemove.run());
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        add(removeButton, gbc);

        JLabel widthLabel = smallLabel("Width (ft)");
        JLabel lengthLabel = smallLabel("Length (ft)");
        widthField.setColumns(6);
        lengthField.setColumns(6);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(widthLabel, gbc);
        gbc.gridx = 2;
        add(lengthLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(widthField, gbc);
        gbc.gridx = 2;
        add(lengthField, gbc);

        sealerCheck.setOpaque(false);
        sealerCheck.setForeground(Theme.TEXT_LIGHT);
        sealerCheck.setFont(Theme.FONT_BODY);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        add(sealerCheck, gbc);
    }

    private JLabel smallLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.GOLD);
        label.setFont(Theme.FONT_BOLD);
        return label;
    }

    public void setDisplayIndex(int index) {
        titleLabel.setText("PROPERTY " + (index + 1));
    }

    /** Returns the area in square feet. Throws NumberFormatException on bad/blank input. */
    public double area() {
        double width = Double.parseDouble(widthField.getText().trim());
        double length = Double.parseDouble(lengthField.getText().trim());
        if (width <= 0 || length <= 0) {
            throw new NumberFormatException("Width and length must be positive numbers.");
        }
        return width * length;
    }

    public boolean isSealerApplied() {
        return sealerCheck.isSelected();
    }
}
