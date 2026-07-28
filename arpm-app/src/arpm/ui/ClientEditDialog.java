package arpm.ui;

import arpm.model.Client;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Modal dialog used to create a new client profile or edit an existing one.
 */
public class ClientEditDialog extends JDialog {

    private final Client existing;
    private Client result;

    private final JTextField nameField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField jobTotalField = new JTextField();
    private final JSpinner transactionDateSpinner = makeDateSpinner();
    private final JCheckBox scheduledCheck = new JCheckBox("Assign a service date now");
    private final JSpinner serviceDateSpinner = makeDateSpinner();

    public ClientEditDialog(Frame owner, Client existing) {
        super(owner, existing == null ? "Add Client" : "Edit Client", true);
        this.existing = existing;
        setSize(480, 560);
        setLocationRelativeTo(owner);
        setResizable(false);

        GradientPanel content = new GradientPanel(Theme.NAVY, Theme.NAVY_DARK);
        content.setLayout(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setContentPane(content);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        serviceDateSpinner.setEnabled(false);
        scheduledCheck.setForeground(Theme.TEXT_LIGHT);
        scheduledCheck.setFont(Theme.FONT_BODY);
        scheduledCheck.setOpaque(false);
        scheduledCheck.addActionListener(e -> serviceDateSpinner.setEnabled(scheduledCheck.isSelected()));

        int row = 0;
        row = addField(content, gbc, row, "Client Name", nameField);
        row = addField(content, gbc, row, "Address", addressField);

        // Job total field with a button to launch the calculator
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(12, 6, 4, 6);
        content.add(fieldLabel("Job Total ($)"), gbc);
        row++;

        JPanel jobRow = new JPanel(new BorderLayout(8, 0));
        jobRow.setOpaque(false);
        RoundedButton calculatorButton = new RoundedButton("CALCULATOR", Theme.GOLD, Theme.NAVY_DARK);
        calculatorButton.addActionListener(e -> {
            Frame owner2 = (Frame) SwingUtilities.getWindowAncestor(this);
            Double total = CalculatorDialog.showDialog(owner2);
            if (total != null) {
                jobTotalField.setText(String.format("%.2f", total));
            }
        });
        jobRow.add(jobTotalField, BorderLayout.CENTER);
        jobRow.add(calculatorButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 6, 6, 6);
        content.add(jobRow, gbc);
        row++;

        row = addField(content, gbc, row, "Transaction Date", transactionDateSpinner);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(12, 6, 4, 6);
        content.add(scheduledCheck, gbc);
        row++;

        row = addField(content, gbc, row, "Service Date", serviceDateSpinner);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        RoundedButton cancelButton = new RoundedButton("CANCEL", Theme.NAVY_LIGHT, Theme.TEXT_LIGHT);
        RoundedButton saveButton = new RoundedButton("SAVE", Theme.GOLD, Theme.NAVY_DARK);
        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> onSave());
        buttonRow.add(cancelButton);
        buttonRow.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(20, 6, 6, 6);
        content.add(buttonRow, gbc);

        populateFromExisting();
    }

    private void populateFromExisting() {
        if (existing != null) {
            nameField.setText(existing.getName());
            addressField.setText(existing.getAddress());
            jobTotalField.setText(String.format("%.2f", existing.getJobTotal()));
            LocalDate transDate = existing.getTransactionDate() != null ? existing.getTransactionDate() : LocalDate.now();
            transactionDateSpinner.setValue(toDate(transDate));

            if (existing.getServiceDate() != null) {
                scheduledCheck.setSelected(true);
                serviceDateSpinner.setEnabled(true);
                serviceDateSpinner.setValue(toDate(existing.getServiceDate()));
            } else {
                serviceDateSpinner.setValue(toDate(LocalDate.now()));
            }
        } else {
            transactionDateSpinner.setValue(toDate(LocalDate.now()));
            serviceDateSpinner.setValue(toDate(LocalDate.now()));
        }
    }

    private int addField(Container c, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(12, 6, 4, 6);
        c.add(fieldLabel(label), gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(0, 6, 6, 6);
        c.add(field, gbc);
        row++;
        return row;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.GOLD);
        label.setFont(Theme.FONT_BOLD);
        return label;
    }

    private static JSpinner makeDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MM/dd/yyyy"));
        return spinner;
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a client name.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total;
        try {
            total = Double.parseDouble(jobTotalField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Job total must be a number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate transactionDate = toLocalDate((Date) transactionDateSpinner.getValue());
        LocalDate serviceDate = scheduledCheck.isSelected() ? toLocalDate((Date) serviceDateSpinner.getValue()) : null;

        if (existing == null) {
            result = new Client(name, address, total, transactionDate, serviceDate);
        } else {
            existing.setName(name);
            existing.setAddress(address);
            existing.setJobTotal(total);
            existing.setTransactionDate(transactionDate);
            existing.setServiceDate(serviceDate);
            result = existing;
        }
        dispose();
    }

    public Client getResult() {
        return result;
    }
}
