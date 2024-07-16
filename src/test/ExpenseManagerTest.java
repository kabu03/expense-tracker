import com.example.model.Expense;
import com.example.model.ExpenseManager;
import com.example.service.ExpenseService;
import com.example.utils.ExpenseFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.YearMonth;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

// A class for testing with JUnit. I have provided more details in the documentation.
public class ExpenseManagerTest {

    private ExpenseManager expenseManager;
    private ExpenseService expenseService;
    private ExpenseFileHandler expenseFileHandler;

    // Setting up the expenses before each test. I decided to add three expenses, each of different names, dates, amounts and categories.
    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService();
        expenseFileHandler = new ExpenseFileHandler();
        expenseManager = new ExpenseManager(expenseService, expenseFileHandler);
        // Add some default expenses to the manager
        expenseManager.addExpense(new Expense("Lunch", "26/11/2023", "Food", 6200, "HUF"));
        expenseManager.addExpense(new Expense("Bus Ticket", "24/11/2023", "Transportation", 3500, "HUF"));
        expenseManager.addExpense(new Expense("Cheese", "11/10/2023", "Groceries", 4540, "HUF"));
    }

    @Test
    public void testAddExpense() {
        Expense newExpense = new Expense("Coffee", "20/11/2023", "Food", 600, "HUF");
        expenseManager.addExpense(newExpense);
        assertTrue(expenseManager.getAllExpenses().contains(newExpense));
    }

    @Test
    public void testRemoveExpense() {
        Expense expenseToRemove = new Expense("Lunch", "26/11/2023", "Food", 6200, "HUF");
        assertTrue(expenseManager.removeExpense(expenseManager.getAllExpenses().indexOf(expenseToRemove)));
        assertFalse(expenseManager.getAllExpenses().contains(expenseToRemove));
    }

    @Test
    public void testGetExpensesByCategory() {
        List<Expense> foodExpenses = expenseManager.getExpensesByCategory("Food");
        assertEquals(1, foodExpenses.size()); // Assuming only 1 food expense was added in the setup.
    }

    @Test
    public void testAddInvalidExpense() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> expenseManager.addExpense(new Expense("", "invalid-date", "Food", -10.0, "HUF")));
        String expectedMessage = "Invalid date format: " + "invalid-date" + ", please use dd/MM/yyyy.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testRemoveNonExistentExpense() {
        Expense nonExistentExpense = new Expense("Dinner", "07/04/2005", "Food", 9000, "HUF");
        assertFalse(expenseManager.removeExpense(expenseManager.getAllExpenses().indexOf(nonExistentExpense))); // Should return false as it doesn't exist
    }

    @Test
    public void testEditExpense() {
        Expense originalExpense = new Expense("Lunch", "26/11/2023", "Food", 6200, "HUF");
        Expense updatedExpense = new Expense("Lunch", "26/11/2023", "Food", 4500, "HUF"); // Decreased the amount
        expenseManager.editExpense(originalExpense, updatedExpense);
        assertTrue(expenseManager.getAllExpenses().contains(updatedExpense));
        assertFalse(expenseManager.getAllExpenses().contains(originalExpense));
    }

    @Test
    public void testGetExpensesGroupedByMonth() {
        TreeMap<YearMonth, List<Expense>> groupedExpenses = expenseManager.getExpensesGroupedByMonth();
        assertNotNull(groupedExpenses);
        assertFalse(groupedExpenses.isEmpty());
        // Assuming that you have expenses from different months in the setup, assert that:
        assertEquals(2, groupedExpenses.size()); // Since we have expenses tracked in two different months.
    }

    @Test
    public void testClearExpenses() {
        assertFalse(expenseManager.getAllExpenses().isEmpty());
        expenseManager.clearExpenses();
        assertTrue(expenseManager.getAllExpenses().isEmpty());
    }

    @Test
    public void testEqualsAndHashCode() {
        ExpenseManager anotherManager = new ExpenseManager(expenseService, expenseFileHandler);
        anotherManager.addExpense(new Expense("Lunch", "26/11/2023", "Food", 6200, "HUF"));
        anotherManager.addExpense(new Expense("Bus Ticket", "24/11/2023", "Transportation", 3500, "HUF"));
        anotherManager.addExpense(new Expense("Cheese", "11/10/2023", "Groceries", 4540, "HUF"));

        assertEquals(expenseManager, anotherManager);
        assertEquals(expenseManager.hashCode(), anotherManager.hashCode());

        anotherManager.addExpense(new Expense("Extra", "12/11/2023", "Other", 100, "HUF"));
        assertNotEquals(expenseManager, anotherManager);
    }

    // This test method assumes that you will capture the print stream output.
    @Test
    public void testPrintExpenses() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        expenseManager.printExpenses();
        String printedContent = outContent.toString();
        assertTrue(printedContent.contains("Lunch"));
        assertTrue(printedContent.contains("Food"));
        assertTrue(printedContent.contains("6200"));

        // Reset the standard system output to its original.
        System.setOut(System.out);
    }
}