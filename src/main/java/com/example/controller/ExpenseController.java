package com.example.controller;

import com.example.model.Expense;
import com.example.model.ExpenseManager;
import com.example.view.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpenseController {
    public ExpenseManager expenseManager;
    private GUI gui;

    public final String[] CATEGORIES = {"Food", "Rent", "Groceries", "Utilities", "Transportation", "Entertainment", "Other"};
    public final String[] CURRENCIES = {
            "HUF", // Hungarian Forint
            "JOD", // Jordanian Dinar
            "EUR", // Euro
            "USD", // United States Dollar
            "JPY", // Japanese Yen
            "GBP", // British Pound
            "AUD", // Australian Dollar
            "CAD", // Canadian Dollar
            "CHF", // Swiss Franc
            "CNY", // Chinese Yuan
            "SEK", // Swedish Króna
            "NZD", // New Zealand Dollar
            "MXN", // Mexican Peso
            "BHD", // Bahraini Dinar
            "KWD", // Kuwaiti Dinar
            "SGD", // Singapore Dollar
            "NOK", // Norwegian Krone
            "KRW", // South Korean Won
            "TRY", // Turkish Lira
            "RUB", // Russian Ruble
            "INR", // Indian Rupee
            "BRL", // Brazilian Real
            "ZAR", // South African Rand
            "DKK", // Danish Krone
            "PLN", // Polish Zloty
            "TWD", // Taiwan Dollar
            "THB", // Thai Baht
            "IDR", // Indonesian Rupiah
            "CZK", // Czech Koruna
            "AED", // United Arab Emirates Dirham
            "CLP", // Chilean Peso
            "PHP"  // Philippine Peso
    };

    public ExpenseController(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void convertExpenseCurrency(Expense expense, String targetCurrency) {
        try {
            this.expenseManager.getService().convertExpenseCurrency(expense, targetCurrency);
        } catch (IOException e) {
            // Handle the exception appropriately
            System.out.println("The currency could not be converted. There could be an issue linking with the external API.");
        }
    }

    public Map<String, Double> calculateTotalExpensesByCategory() {
        return expenseManager.calculateTotalExpensesByCategory();
    }

    public List<Expense> getAllExpenses() {
        return expenseManager.getAllExpenses();
    }

    public List<Expense> loadExpenses() throws IOException, ClassNotFoundException {
        return expenseManager.loadExpenses();
    }

    public void saveExpenses() throws IOException {
        expenseManager.saveExpenses();
    }

    public TreeMap<YearMonth, List<Expense>> getExpensesGroupedByMonth() {
        return expenseManager.getExpensesGroupedByMonth();
    }

    public boolean addExpense(String name, String date, String category, String amount, String currency) {
        try {
            double amountValue = Double.parseDouble(amount);
            Expense newExpense = new Expense(name, date, category, amountValue, currency);
            expenseManager.addExpense(newExpense);
            gui.updateMonthComboBox();
            // Update the JTable.
            gui.tableModel.addRow(new Object[]{name, date, category, amountValue, currency});
            if (gui.totalsPanelVisible) gui.updateTotalExpensesByCategoryDisplay();
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void removeSelectedExpense() {
        int selectedRow = gui.expensesTable.getSelectedRow();
        if (selectedRow != -1) {
            int response = JOptionPane.showConfirmDialog(gui, // Confirmation message.
                    "Are you sure you want to delete the selected expense?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                int modelIndex = gui.expensesTable.convertRowIndexToModel(selectedRow);
                if (expenseManager.removeExpense(modelIndex)) {
                    gui.tableModel.removeRow(modelIndex);
                    gui.updateMonthComboBox();
                    if (gui.totalsPanelVisible) gui.updateTotalExpensesByCategoryDisplay();
                }
            }
        } else {
            JOptionPane.showMessageDialog(gui, "Please select an expense to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }


    public void editSelectedExpense() {
        int selectedRow = gui.expensesTable.getSelectedRow();
        if (selectedRow != -1) {
            // Determine if a month is selected and get the corresponding list of expenses
            YearMonth selectedMonth = (YearMonth) gui.monthComboBox.getSelectedItem();
            List<Expense> relevantExpenses = selectedMonth == null ? getAllExpenses() : getExpensesGroupedByMonth().get(selectedMonth);

            if (relevantExpenses != null && !relevantExpenses.isEmpty()) {
                int modelIndex = gui.expensesTable.convertRowIndexToModel(selectedRow);
                if (modelIndex < relevantExpenses.size()) {
                    Expense oldExpense = relevantExpenses.get(modelIndex);
                    Expense newExpense = getUpdatedExpenseFromUser(oldExpense); // This method gets the updated expense from the user

                    if (newExpense != null) {
                        expenseManager.editExpense(oldExpense, newExpense);
                        gui.updateTableRow(newExpense, modelIndex);
                        gui.updateMonthComboBox();
                        if (gui.totalsPanelVisible) gui.updateTotalExpensesByCategoryDisplay();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(gui, "Please select an expense to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Expense getUpdatedExpenseFromUser(Expense existingExpense) {
        // Creating text fields pre-populated with the existing expense's data, to make it more user-friendly.
        JTextField nameField = new JTextField(existingExpense.getName());
        JTextField dateField = new JTextField(existingExpense.getDate());
        JTextField amountField = new JTextField(String.valueOf(existingExpense.getAmount()));
        JComboBox<String> categoryComboBox = new JComboBox<>(CATEGORIES);
        JComboBox<String> currencyComboBox = new JComboBox<>(CURRENCIES);
        currencyComboBox.setSelectedItem(existingExpense.getCurrency());
        categoryComboBox.setSelectedItem(existingExpense.getCategory());

        // Create a panel to hold these components, similar to the addExpenseDialog
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Convert Currency"));
        panel.add(currencyComboBox);
        // Display the dialog and get the user's input.
        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Expense, check the data first!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Parse the amount as a double and return a new Expense object with the updated data.
                double amountValue = Double.parseDouble(amountField.getText());
                Expense updatedExpense = new Expense(nameField.getText(), dateField.getText(), (String) categoryComboBox.getSelectedItem(), amountValue, existingExpense.getCurrency());
                String chosenCurrency = (String) currencyComboBox.getSelectedItem();
                this.convertExpenseCurrency(updatedExpense, chosenCurrency);
                return updatedExpense;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid amount format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null; // Indicate to the caller that the update was not successful.
            }
        } else {
            return null;
        }
    }
}
