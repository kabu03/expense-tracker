import com.example.model.Expense;
import com.example.model.ExpenseManager;
import com.example.service.ExpenseService;
import com.example.utils.ExpenseFileHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// A class for testing with JUnit. I have provided more details in the documentation.
public class ExpenseFileHandlerTest {
    private final String testFileName = "testExpenses.ser"; // Using a test file to avoid overwriting real data
    private ExpenseFileHandler handler;
    private final ExpenseService expenseService = new ExpenseService();
    private final ExpenseFileHandler expenseFileHandler = new ExpenseFileHandler();
    private final ExpenseManager testExpenseManager = new ExpenseManager(expenseService, expenseFileHandler);

    @BeforeEach
    public void setUp() throws IOException {
        handler = new ExpenseFileHandler(testFileName);
        testExpenseManager.addExpense(new Expense("Lunch", "10/10/2023", "Food", 2300, "HUF"));
        testExpenseManager.addExpense(new Expense("Netflix", "11/10/2023", "Entertainment", 5750, "HUF"));
        // Save the test expenses to the file
        handler.saveExpensesToFile(testExpenseManager.getAllExpenses());
    }

    @Test
    public void testSaveExpensesToFile() throws IOException, ClassNotFoundException {
        // Load the expenses from the test file
        List<Expense> loadedExpenses = handler.loadExpensesFromFile();
        // Check that the loaded expenses match what was saved
        assertEquals(testExpenseManager.getAllExpenses().size(), loadedExpenses.size());
        for (int i = 0; i < testExpenseManager.getAllExpenses().size(); i++) {
            assertEquals(testExpenseManager.getAllExpenses().get(i), loadedExpenses.get(i));
        }
    }

    @Test
    public void testLoadExpensesFromFile() throws IOException, ClassNotFoundException {
        // Directly load the expenses from the test file
        List<Expense> loadedExpenses = handler.loadExpensesFromFile();
        // Check that the loaded expenses match the test data
        assertEquals(testExpenseManager.getAllExpenses().size(), loadedExpenses.size());
        for (int i = 0; i < testExpenseManager.getAllExpenses().size(); i++) {
            assertEquals(testExpenseManager.getAllExpenses().get(i).getName(), loadedExpenses.get(i).getName());
            assertEquals(testExpenseManager.getAllExpenses().get(i).getDate(), loadedExpenses.get(i).getDate());
            assertEquals(testExpenseManager.getAllExpenses().get(i).getCategory(), loadedExpenses.get(i).getCategory());
            assertEquals(testExpenseManager.getAllExpenses().get(i).getAmount(), loadedExpenses.get(i).getAmount());
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up the test file
        new File(testFileName).delete();
    }
}
