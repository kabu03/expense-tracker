import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private final List<Expense> expenses;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Allowed date-time format.

    public ExpenseManager() {
        expenses = new LinkedList<>();
    } // Storing all expenses in a linked list.

    public void addExpense(Expense expense) {
        if (expense != null) {
            expenses.add(expense);
        }
    }

    public boolean removeExpense(Expense expense) {
        if (expense == null) {
            return false;
        }
        return expenses.remove(expense);
    }

    public void editExpense(Expense oldExpense, Expense newExpense) {
        if (oldExpense == null || newExpense == null) {
            return;
        }

        int index = expenses.indexOf(oldExpense);
        if (index != -1) {
            expenses.set(index, newExpense);
        }
    }

    public List<Expense> getAllExpenses() {
        return expenses;
    }

    public void printExpenses() { // Useful for debugging.
        if (expenses.isEmpty()) {
            System.out.println("No expenses to display.");
            return;
        }

        for (Expense expense : expenses) {
            System.out.println("Expense Name: " + expense.getName());
            System.out.println("Expense Category: " + expense.getCategory());
            System.out.println("Expense Amount: " + expense.getAmount());
            System.out.println("Expense Date: " + expense.getDate());
        }
    }

    public void clearExpenses() {
        expenses.clear();
    }

    // The following method will return a list of expenses that fall within a certain category.
    public List<Expense> getExpensesByCategory(String categoryName) {
        if (categoryName == null) {
            return new ArrayList<>();
        }

        return expenses.stream().filter(expense -> categoryName.equals(expense.getCategory())).collect(Collectors.toList());
    }

    // I used a map to calculate total expenses by category, so each category (String) is mapped to the amount (Double).
    public Map<String, Double> calculateTotalExpensesByCategory() {
        Map<String, Double> totals = new HashMap<>();
        String[] categories = {"Food", "Rent", "Groceries", "Utilities", "Transportation", "Entertainment", "Other"};
        for (String category : categories) {
            List<Expense> expensesByCategory = getExpensesByCategory(category);
            double sum = expensesByCategory.stream().mapToDouble(Expense::getAmount).sum();
            totals.put(category, sum);
        }
        return totals;
    }

    // I used a TreeMap and java.time to group the expenses by month.
    public TreeMap<YearMonth, List<Expense>> getExpensesGroupedByMonth() {
        TreeMap<YearMonth, List<Expense>> groupedByMonth = new TreeMap<>();
        for (Expense expense : expenses) {
            LocalDate date = LocalDate.parse(expense.getDate(), formatter);
            YearMonth yearMonth = YearMonth.from(date);
            groupedByMonth.computeIfAbsent(yearMonth, k -> new ArrayList<>()).add(expense);
        }
        return groupedByMonth;
    }


    // Equals and hashCode methods for proper comparison.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpenseManager that = (ExpenseManager) o;

        // Assuming equality is based on having the exact same expenses
        return Objects.equals(expenses, that.expenses);
    }

    @Override
    public int hashCode() {
        return expenses.hashCode();
    }
}