package arpm.ui;

import arpm.model.Client;
import arpm.store.ClientStore;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Screen for assigning client jobs to specific calendar days, and for
 * reviewing/removing those assignments.
 */
public class ScheduleCalendarPanel extends JPanel {

    private final ClientStore store;
    private YearMonth currentMonth;
    private LocalDate selectedDay;

    private final JLabel monthLabel = new JLabel();
    private final JPanel gridPanel = new JPanel(new GridLayout(0, 7, 8, 8));
    private final JPanel sidePanel = new JPanel(new BorderLayout());

    public ScheduleCalendarPanel(ClientStore store, Runnable onBack) {
        this.store = store;
        this.currentMonth = YearMonth.now();

        setLayout(new BorderLayout());
        add(new HeaderBar("SCHEDULE CALENDAR", onBack), BorderLayout.NORTH);

        GradientPanel content = new GradientPanel(Theme.NAVY, Theme.NAVY_DARK);
        content.setLayout(new BorderLayout(20, 0));
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel calendarBlock = new JPanel();
        calendarBlock.setOpaque(false);
        calendarBlock.setLayout(new BoxLayout(calendarBlock, BoxLayout.Y_AXIS));

        JPanel navRow = new JPanel(new BorderLayout());
        navRow.setOpaque(false);

        RoundedButton prevButton = new RoundedButton("\u2039", Theme.GOLD, Theme.NAVY_DARK);
        RoundedButton nextButton = new RoundedButton("\u203A", Theme.GOLD, Theme.NAVY_DARK);
        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            selectedDay = null;
            rebuild();
        });
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            selectedDay = null;
            rebuild();
        });

        monthLabel.setFont(Theme.FONT_TITLE);
        monthLabel.setForeground(Theme.GOLD);
        monthLabel.setHorizontalAlignment(JLabel.CENTER);

        navRow.add(prevButton, BorderLayout.WEST);
        navRow.add(monthLabel, BorderLayout.CENTER);
        navRow.add(nextButton, BorderLayout.EAST);

        JPanel dayNamesRow = new JPanel(new GridLayout(1, 7, 8, 0));
        dayNamesRow.setOpaque(false);
        for (String dayName : new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}) {
            JLabel label = new JLabel(dayName, JLabel.CENTER);
            label.setForeground(Theme.GOLD);
            label.setFont(Theme.FONT_BOLD);
            dayNamesRow.add(label);
        }

        gridPanel.setOpaque(false);

        calendarBlock.add(navRow);
        calendarBlock.add(Box.createVerticalStrut(12));
        calendarBlock.add(dayNamesRow);
        calendarBlock.add(Box.createVerticalStrut(6));
        calendarBlock.add(gridPanel);
        calendarBlock.add(Box.createVerticalStrut(12));
        calendarBlock.add(buildLegend());

        sidePanel.setOpaque(true);
        sidePanel.setBackground(Theme.NAVY_LIGHT);
        sidePanel.setBorder(BorderFactory.createLineBorder(Theme.GOLD, 1));
        sidePanel.setPreferredSize(new Dimension(340, 10));

        content.add(calendarBlock, BorderLayout.CENTER);
        content.add(sidePanel, BorderLayout.EAST);

        add(content, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                rebuild();
            }
        });

        rebuild();
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legend.setOpaque(false);
        legend.add(legendItem(Theme.GOLD, "Selected day", false));
        legend.add(legendItem(Theme.GREEN, "Jobs assigned", false));
        legend.add(legendItem(Theme.NAVY, "Today", true));
        return legend;
    }

    private JPanel legendItem(Color color, String text, boolean outlineOnly) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);

        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(16, 16));
        if (outlineOnly) {
            swatch.setBackground(Theme.NAVY);
            swatch.setBorder(BorderFactory.createLineBorder(Theme.GOLD, 2));
        } else {
            swatch.setBackground(color);
        }

        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT_LIGHT);
        label.setFont(Theme.FONT_BODY);

        item.add(swatch);
        item.add(label);
        return item;
    }

    private void rebuild() {
        monthLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + currentMonth.getYear());
        gridPanel.removeAll();

        LocalDate firstOfMonth = currentMonth.atDay(1);
        DayOfWeek firstDow = firstOfMonth.getDayOfWeek();
        int leadingBlanks = (firstDow == DayOfWeek.SUNDAY) ? 0 : firstDow.getValue();

        for (int i = 0; i < leadingBlanks; i++) {
            JPanel blank = new JPanel();
            blank.setOpaque(false);
            gridPanel.add(blank);
        }

        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            long jobCount = store.getAll().stream().filter(c -> date.equals(c.getServiceDate())).count();
            gridPanel.add(buildDayCell(date, jobCount, date.equals(today), date.equals(selectedDay)));
        }

        gridPanel.revalidate();
        gridPanel.repaint();
        rebuildSidePanel();
    }

    private JButton buildDayCell(LocalDate date, long jobCount, boolean isToday, boolean isSelected) {
        JButton cell = new JButton();
        cell.setLayout(new BorderLayout());
        cell.setFocusPainted(false);
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cell.setPreferredSize(new Dimension(90, 70));
        cell.setOpaque(true);

        Color background = Theme.NAVY_LIGHT;
        if (jobCount > 0) {
            background = Theme.GREEN;
        }
        if (isSelected) {
            background = Theme.GOLD;
        }
        cell.setBackground(background);
        cell.setBorder(BorderFactory.createLineBorder(isToday ? Theme.GOLD : background.darker(), isToday ? 2 : 1));

        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayLabel.setHorizontalAlignment(JLabel.CENTER);
        dayLabel.setFont(Theme.FONT_BOLD);
        dayLabel.setForeground(isSelected ? Theme.NAVY_DARK : Theme.TEXT_LIGHT);
        cell.add(dayLabel, BorderLayout.CENTER);

        if (jobCount > 0) {
            JLabel jobsLabel = new JLabel(jobCount + (jobCount == 1 ? " job" : " jobs"));
            jobsLabel.setHorizontalAlignment(JLabel.CENTER);
            jobsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            jobsLabel.setForeground(isSelected ? Theme.NAVY_DARK : new Color(0xD9, 0xFF, 0xE0));
            cell.add(jobsLabel, BorderLayout.SOUTH);
        }

        cell.addActionListener(e -> {
            selectedDay = date;
            rebuild();
        });

        return cell;
    }

    private void rebuildSidePanel() {
        sidePanel.removeAll();

        if (selectedDay == null) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20));

            JLabel icon = new JLabel("\uD83D\uDCC5");
            icon.setFont(new Font("SansSerif", Font.PLAIN, 40));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel message = new JLabel("Click a day to manage scheduled jobs.");
            message.setForeground(Theme.TEXT_LIGHT);
            message.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createVerticalStrut(16));
            empty.add(message);

            sidePanel.add(empty, BorderLayout.NORTH);
            sidePanel.revalidate();
            sidePanel.repaint();
            return;
        }

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("<html>" + selectedDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US)
                + ",<br>" + selectedDay.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                + " " + selectedDay.getDayOfMonth() + ", " + selectedDay.getYear() + "</html>");
        title.setForeground(Theme.GOLD);
        title.setFont(Theme.FONT_BOLD);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(title);
        wrap.add(Box.createVerticalStrut(16));

        List<Client> assigned = store.getAll().stream()
                .filter(c -> selectedDay.equals(c.getServiceDate()))
                .collect(Collectors.toList());

        if (assigned.isEmpty()) {
            JLabel none = new JLabel("No jobs scheduled for this day.");
            none.setForeground(Theme.TEXT_MUTED);
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrap.add(none);
        } else {
            for (Client c : assigned) {
                wrap.add(buildAssignedRow(c));
                wrap.add(Box.createVerticalStrut(8));
            }
        }

        wrap.add(Box.createVerticalStrut(20));

        JLabel assignTitle = new JLabel("Assign a client");
        assignTitle.setForeground(Theme.GOLD);
        assignTitle.setFont(Theme.FONT_BOLD);
        assignTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(assignTitle);
        wrap.add(Box.createVerticalStrut(8));

        List<Client> allClients = store.getAll();

        if (allClients.isEmpty()) {
            JLabel noClients = new JLabel("Add a client profile first.");
            noClients.setForeground(Theme.TEXT_MUTED);
            noClients.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrap.add(noClients);
        } else {
            JComboBox<Client> clientCombo = new JComboBox<>(allClients.toArray(new Client[0]));
            clientCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Client) {
                        setText(((Client) value).getName());
                    }
                    return this;
                }
            });
            clientCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            clientCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            RoundedButton assignButton = new RoundedButton("ASSIGN TO THIS DAY", Theme.GOLD, Theme.NAVY_DARK);
            assignButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            assignButton.addActionListener(e -> {
                Client selected = (Client) clientCombo.getSelectedItem();
                if (selected != null) {
                    selected.setServiceDate(selectedDay);
                    store.update();
                    rebuild();
                }
            });

            wrap.add(clientCombo);
            wrap.add(Box.createVerticalStrut(10));
            wrap.add(assignButton);
        }

        JScrollPane scroll = new JScrollPane(wrap);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(scroll, BorderLayout.CENTER);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    private JPanel buildAssignedRow(Client c) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(Theme.NAVY);
        row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(c.getName());
        nameLabel.setForeground(Theme.TEXT_LIGHT);
        nameLabel.setFont(Theme.FONT_BOLD);

        JLabel addressLabel = new JLabel(c.getAddress());
        addressLabel.setForeground(Theme.TEXT_MUTED);
        addressLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        textPanel.add(nameLabel);
        textPanel.add(addressLabel);

        RoundedButton removeButton = new RoundedButton("REMOVE", Theme.RED_DARK, Theme.RED_TEXT);
        removeButton.addActionListener(e -> {
            c.setServiceDate(null);
            store.update();
            rebuild();
        });

        row.add(textPanel, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);
        return row;
    }
}
