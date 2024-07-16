package com.example.view;

import com.example.controller.ExpenseController;
import com.example.model.Expense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.*;

public class GUI extends JFrame {
    public JTable expensesTable; // The table storing the expenses.
    public DefaultTableModel tableModel; // The table model to use for the expenses table (default).
    private final ExpenseController controller; // The controller to handle user actions.
    private JButton showTotalsButton;
    private JPanel totalsPanel; // This panel will store the totals for every category.
    public JComboBox<YearMonth> monthComboBox; // This combo box will let the user choose a month to filter expenses.
    public boolean totalsPanelVisible = false;

    public GUI(ExpenseController controller) {
        this.controller = controller;
        initializeMonthComboBox();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Expense Tracker");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Cleaner when it opens maximized.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        loadMenuItem.addActionListener(e -> loadExpenses());
        saveMenuItem.addActionListener(e -> saveExpenses());
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        setJMenuBar(menuBar); // Adding menu bar to the frame, that has a File menu with Load and Save items.
        setupTable(); // Separate method for setting the table up.
        JPanel southPanel = getSouthPanel(); // Separate method for getting the South Panel.
        setLayout(new BorderLayout());
        add(new JScrollPane(expensesTable), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JPanel getSouthPanel() {
        // Panel for operation buttons (add, edit, delete) in a separate method.
        JPanel buttonPanel = getOperationButtonPanel();
        monthComboBox.setPreferredSize(new Dimension(200, 20));
        // Creating a panel for the month combo box.
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.add(new JLabel("Filter by Month:"));
        filterPanel.add(monthComboBox);
        // Button to show/hide totals
        showTotalsButton = new JButton("Show Overall Spending (JOD)");
        showTotalsButton.addActionListener(e -> toggleTotalsPanelVisibility());
        // Panel for the show totals button
        JPanel showTotalsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        showTotalsPanel.add(showTotalsButton);
        totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Totals:");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalsPanel.add(titleLabel);
        totalsPanel.setVisible(false); // Initially hide the totals panel
        int top = 0, left = 20, bottom = 0, right = 50;
        totalsPanel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.WEST);
        southPanel.add(filterPanel, BorderLayout.CENTER);
        Box eastBox = Box.createHorizontalBox(); // Create a Box container with horizontal layout
        eastBox.add(totalsPanel); // Add the totalsPanel first so it appears to the left
        eastBox.add(Box.createHorizontalStrut(10)); // Add some horizontal space between the totalsPanel and the showTotalsPanel
        eastBox.add(showTotalsPanel); // Add the showTotalsPanel next so it appears to the right
        southPanel.add(eastBox, BorderLayout.EAST); // Now, add the entire Box container to the EAST of the southPanel
        return southPanel;
    }

    private void toggleTotalsPanelVisibility() {
        // Toggle the visibility state of totalsPanel
        totalsPanel.setVisible(!totalsPanel.isVisible());

        // Update totalsPanelVisible based on the current visibility of totalsPanel
        totalsPanelVisible = totalsPanel.isVisible();

        // Set the button text based on the new visibility state
        if (totalsPanelVisible) {
            showTotalsButton.setText("Hide Overall Spending");
            updateTotalExpensesByCategoryDisplay(); // Update the panel when making it visible
        } else {
            showTotalsButton.setText("Show Overall Spending (JOD)");
        }
    }

    private JPanel getOperationButtonPanel() {
        JButton addExpenseButton = new JButton("Add New Expense | A");
        Dimension buttonSize = new Dimension(400, 45);
        addExpenseButton.setPreferredSize(buttonSize);
        addExpenseButton.setMaximumSize(buttonSize);
        addExpenseButton.setMinimumSize(buttonSize);
        addExpenseButton.addActionListener(e -> showAddExpenseDialog());
        addExpenseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A'), "AddNewExpense");
        addExpenseButton.getActionMap().put("AddNewExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddExpenseDialog();
            }
        });
        addExpenseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('a'), "AddNewExpense");
        addExpenseButton.getActionMap().put("AddNewExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddExpenseDialog();
            }
        });
        JButton editExpenseButton = new JButton("Edit Expense | E");
        editExpenseButton.setPreferredSize(buttonSize);
        editExpenseButton.setMaximumSize(buttonSize);
        editExpenseButton.setMinimumSize(buttonSize);
        editExpenseButton.addActionListener(e -> controller.editSelectedExpense());
        addExpenseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('E'), "EditExpense");
        addExpenseButton.getActionMap().put("EditExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.editSelectedExpense();
            }
        });
        addExpenseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('e'), "EditExpense");
        addExpenseButton.getActionMap().put("EditExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.editSelectedExpense();
            }
        });
        JButton deleteExpenseButton = new JButton("Delete Expense | D");
        deleteExpenseButton.setPreferredSize(buttonSize);
        deleteExpenseButton.setMaximumSize(buttonSize);
        deleteExpenseButton.setMinimumSize(buttonSize);
        deleteExpenseButton.addActionListener(e -> controller.removeSelectedExpense());
        addExpenseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('D'), "RemoveExpense");
        addExpenseButton.getActionMap().put("RemoveExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.removeSelectedExpense();
            }
        });
        addExpenseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('d'), "RemoveExpense");
        addExpenseButton.getActionMap().put("RemoveExpense", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.removeSelectedExpense();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(editExpenseButton);
        buttonPanel.add(deleteExpenseButton);
        return buttonPanel;
    }

    private void loadExpenses() {
        try {
            List<Expense> loadedExpenses = controller.loadExpenses();
            // Clear existing data first.
            tableModel.setRowCount(0);

            // Add loaded expenses to the table.
            for (Expense expense : loadedExpenses) {
                tableModel.addRow(new Object[]{expense.getName(), expense.getDate(), expense.getCategory(), expense.getAmount(), expense.getCurrency()});
            }
            JOptionPane.showMessageDialog(this, "Expenses loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (totalsPanelVisible) updateTotalExpensesByCategoryDisplay();
            updateMonthComboBox();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading expenses: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveExpenses() {
        try {
            controller.saveExpenses();
            JOptionPane.showMessageDialog(this, "Expenses saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving expenses: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTable() {
        String[] columnNames = {"Description", "Date", "Category", "Amount", "Currency"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) { // 'Amount' is the fourth column (0-indexed).
                    return Double.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // This will make none of the cells editable, to prevent data inconsistencies.
                return false;
            }
        };
        expensesTable = new JTable(tableModel);
        expensesTable.setAutoCreateRowSorter(true);
    }

    public void updateTotalExpensesByCategoryDisplay() {
        Map<String, Double> totals = controller.calculateTotalExpensesByCategory();
        totalsPanel.removeAll();
        totalsPanel.add(new JLabel("Overall Spending (in JOD):"));
        refreshTotalsPanel(totals);
    }

    private void refreshTotalsPanel(Map<String, Double> totals) {
        for (Map.Entry<String, Double> total : totals.entrySet()) {
            JLabel totalLabel = new JLabel(total.getKey() + ": " + String.format("%.2f", total.getValue()));
            totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            totalsPanel.add(totalLabel);
        }
        totalsPanel.revalidate();
        totalsPanel.repaint();
    }

    private void showAddExpenseDialog() {
        boolean validExpenseAdded = false;
        JTextField nameField = new JTextField(10);
        JTextField dateField = new JTextField(10);
        JTextField amountField = new JTextField(10);
        JComboBox<String> categoryComboBox = new JComboBox<>(controller.CATEGORIES);
        JComboBox<String> currencyComboBox = new JComboBox<>(controller.CURRENCIES);

        while (!validExpenseAdded) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Date (optional):"));
            panel.add(dateField);
            panel.add(new JLabel("Category:"));
            panel.add(categoryComboBox);
            panel.add(new JLabel("Amount:"));
            panel.add(amountField);
            panel.add(new JLabel("Currency:"));
            panel.add(currencyComboBox);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add New Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String date = dateField.getText().isEmpty() ? "06/06/2003" : dateField.getText(); // Default to current date if empty
                validExpenseAdded = controller.addExpense(nameField.getText(), date, (String) categoryComboBox.getSelectedItem(), amountField.getText(), (String) currencyComboBox.getSelectedItem());
            } else {
                break; // Exit the loop if the user cancels the dialog
            }
        }
    }

    public void updateTableRow(Expense expense, int rowIndex) {
        tableModel.setValueAt(expense.getName(), rowIndex, 0);
        tableModel.setValueAt(expense.getDate(), rowIndex, 1);
        tableModel.setValueAt(expense.getCategory(), rowIndex, 2);
        tableModel.setValueAt(expense.getAmount(), rowIndex, 3);
        tableModel.setValueAt(expense.getCurrency(), rowIndex, 4);
    }

    private void initializeMonthComboBox() {
        monthComboBox = new JComboBox<>();
        TreeMap<YearMonth, List<Expense>> groupedByMonth = controller.getExpensesGroupedByMonth();
        // Using a Vector for a "Show All" option at the beginning of the list.
        Vector<YearMonth> comboBoxModelData = new Vector<>(groupedByMonth.keySet());
        comboBoxModelData.insertElementAt(null, 0);

        DefaultComboBoxModel<YearMonth> model = new DefaultComboBoxModel<>(comboBoxModelData);
        monthComboBox.setModel(model);

        monthComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Show All");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });

        monthComboBox.addActionListener(e -> {
            YearMonth selectedMonth = (YearMonth) monthComboBox.getSelectedItem();
            if (selectedMonth == null) {
                updateTableForAllExpenses(); // This will update the table for all expenses.
                updateTotalExpensesByCategoryDisplay(); // This will update the totals for all expenses.
            } else {
                updateTableForSelectedMonth(selectedMonth); // This updates the table for the selected month.
                updateTotalsForSelectedMonth(); // This updates totals for the selected month.
            }
        });
    }

    private void updateTableForAllExpenses() {
        List<Expense> allExpenses = controller.getAllExpenses();
        tableModel.setRowCount(0); // Clear the table first.
        for (Expense expense : allExpenses) {
            tableModel.addRow(new Object[]{expense.getName(), expense.getDate(), expense.getCategory(), expense.getAmount(), expense.getCurrency()});
        }
    }

    private void updateTableForSelectedMonth(YearMonth yearMonth) {
        List<Expense> expensesForMonth = controller.getExpensesGroupedByMonth().get(yearMonth);
        tableModel.setRowCount(0); // Clear the table first.
        for (Expense expense : expensesForMonth) {
            tableModel.addRow(new Object[]{expense.getName(), expense.getDate(), expense.getCategory(), expense.getAmount(), expense.getCurrency()});
            updateTotalsForSelectedMonth();
        }
    }

    public void updateMonthComboBox() {
        TreeMap<YearMonth, List<Expense>> groupedByMonth = controller.getExpensesGroupedByMonth();
        DefaultComboBoxModel<YearMonth> model = new DefaultComboBoxModel<>();
        model.addElement(null);  // Represents the "Show All" option.
        // Adding the rest of the months.
        for (YearMonth yearMonth : groupedByMonth.keySet()) {
            model.addElement(yearMonth);
        }
        monthComboBox.setModel(model);
        monthComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Show All");
                } else {
                    setText(((YearMonth) value).toString());
                }
                return this;
            }
        });
    }

    private void updateTotalsForSelectedMonth() {
        // Reset the totals panel first.
        totalsPanel.removeAll();

        // Initialize a map to hold the totals for each category.
        Map<String, Double> totals = new LinkedHashMap<>();

        // Initialize all categories with zero totals.
        for (String category : controller.CATEGORIES) {
            totals.put(category, 0.0);
        }

        // Iterate over the table model to accumulate totals by category.
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String category = (String) tableModel.getValueAt(i, 2); // Zero-indexed, so 2 is the category column index
            Double amount = (Double) tableModel.getValueAt(i, 3); // Zero-indexed, so 3 is the amount column index
            totals.merge(category, amount, Double::sum);
        }

        // Add labels for each category total in the predefined order of CATEGORIES.
        double overallSpending = 0;
        for (String category : controller.CATEGORIES) {
            Double totalAmount = totals.get(category);
            overallSpending += totalAmount;
            JLabel totalLabel = new JLabel(category + ": " + String.format("%.2f", totalAmount));
            totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            totalsPanel.add(totalLabel);
        }

        // Add overall spending label.
        JLabel overallSpendingLabel = new JLabel("Overall Spending: " + String.format("%.2f", overallSpending));
        overallSpendingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalsPanel.add(overallSpendingLabel, 0);

        // Refresh the panel to display the new totals.
        totalsPanel.revalidate();
        totalsPanel.repaint();
    }
}
