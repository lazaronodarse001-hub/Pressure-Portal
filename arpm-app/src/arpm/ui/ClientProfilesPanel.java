package arpm.ui;

import arpm.model.Client;
import arpm.store.ClientStore;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Screen listing all client profiles, with the ability to add, edit, and
 * delete records.
 */
public class ClientProfilesPanel extends JPanel {

    private static final double[] COL_WEIGHTS = {1.3, 1.6, 0.9, 1.0, 1.0, 1.1};
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter DATE_FMT_LONG = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final ClientStore store;
    private final JPanel listPanel = new JPanel();
    private final JLabel countLabel = new JLabel();

    public ClientProfilesPanel(ClientStore store, Runnable onBack) {
        this.store = store;
        setLayout(new BorderLayout());
        add(new HeaderBar("CLIENT PROFILES", onBack), BorderLayout.NORTH);

        GradientPanel content = new GradientPanel(Theme.NAVY, Theme.NAVY_DARK);
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        countLabel.setForeground(Theme.TEXT_LIGHT);
        countLabel.setFont(Theme.FONT_BODY);

        RoundedButton addButton = new RoundedButton("+ ADD CLIENT", Theme.GOLD, Theme.NAVY_DARK);
        addButton.addActionListener(e -> openEditor(null));

        topBar.add(countLabel, BorderLayout.WEST);
        topBar.add(addButton, BorderLayout.EAST);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.GOLD, 1));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        content.add(topBar, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });

        refresh();
    }

    private void refresh() {
        listPanel.removeAll();

        listPanel.add(buildRow(new Component[]{
                headerLabel("Client Name"),
                headerLabel("Address"),
                headerLabel("Job Total"),
                headerLabel("Transaction Date"),
                headerLabel("Service Date"),
                headerLabel("")
        }, true));

        List<Client> clients = store.getAll();
        countLabel.setText(clients.size() + (clients.size() == 1 ? " client on record" : " clients on record"));

        for (Client c : clients) {
            JLabel nameLabel = cellLabel(c.getName(), true);
            JLabel addressLabel = cellLabel(c.getAddress(), false);

            JLabel totalLabel = cellLabel(String.format("$%.2f", c.getJobTotal()), true);
            totalLabel.setForeground(Theme.GOLD);

            JLabel transLabel = cellLabel(
                    c.getTransactionDate() != null ? DATE_FMT.format(c.getTransactionDate()) : "\u2014", false);
            JLabel serviceLabel = cellLabel(
                    c.getServiceDate() != null ? DATE_FMT_LONG.format(c.getServiceDate()) : "Not scheduled", false);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            actions.setOpaque(false);

            RoundedButton editButton = new RoundedButton("EDIT", Theme.GOLD, Theme.NAVY_DARK);
            RoundedButton deleteButton = new RoundedButton("DELETE", Theme.RED_DARK, Theme.RED_TEXT);

            editButton.addActionListener(e -> openEditor(c));
            deleteButton.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Delete client \"" + c.getName() + "\"? This cannot be undone.",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    store.remove(c);
                    refresh();
                }
            });

            actions.add(editButton);
            actions.add(deleteButton);

            listPanel.add(buildRow(new Component[]{nameLabel, addressLabel, totalLabel, transLabel, serviceLabel, actions}, false));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JLabel headerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.NAVY_DARK);
        return label;
    }

    private JLabel cellLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(bold ? Theme.FONT_BOLD : Theme.FONT_BODY);
        label.setForeground(Theme.TEXT_LIGHT);
        return label;
    }

    private JPanel buildRow(Component[] cells, boolean header) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(true);
        row.setBackground(header ? Theme.GOLD : Theme.NAVY_LIGHT);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, header ? Theme.GOLD_DARK : Theme.NAVY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 12, 10, 12);

        for (int i = 0; i < cells.length; i++) {
            gbc.gridx = i;
            gbc.weightx = COL_WEIGHTS[i];
            row.add(cells[i], gbc);
        }
        return row;
    }

    private void openEditor(Client existing) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        ClientEditDialog dialog = new ClientEditDialog(owner, existing);
        dialog.setVisible(true);
        Client result = dialog.getResult();
        if (result != null) {
            if (existing == null) {
                store.add(result);
            } else {
                store.update();
            }
            refresh();
        }
    }
}
