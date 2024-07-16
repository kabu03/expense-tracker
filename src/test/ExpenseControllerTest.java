import com.example.controller.ExpenseController;
import com.example.model.Expense;
import com.example.model.ExpenseManager;
import com.example.service.ExpenseService;
import com.example.view.GUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpenseControllerTest {

    private ExpenseController controller;
    private ExpenseManager mockManager;
    private GUI mockGui;
    private ExpenseService mockService;

    @BeforeEach
    void setUp() {
        mockService = mock(ExpenseService.class);
        mockManager = mock(ExpenseManager.class);
        mockGui = mock(GUI.class);
        controller = new ExpenseController(mockManager);
        controller.setGui(mockGui);
        mockGui.tableModel = new DefaultTableModel();
        mockGui.expensesTable = new JTable(mockGui.tableModel);
        when(mockManager.getService()).thenReturn(mockService);

        // Initialize monthComboBox with YearMonth items or Strings, depending on your application's needs
        JComboBox<YearMonth> monthComboBox = new JComboBox<>();
        monthComboBox.addItem(YearMonth.now()); // Add the current month as an item
        // Ensure there's a selected item
        monthComboBox.setSelectedIndex(0);

        // Assuming your GUI class has a public or package-private monthComboBox field
        mockGui.monthComboBox = monthComboBox;
    }

    @Test
    void testAddExpenseSuccessfully() {
        when(mockManager.addExpense(any(Expense.class))).thenReturn(true);
        boolean result = controller.addExpense("Lunch", "01/04/2023", "Food", "10.5", "USD");
        verify(mockManager, times(1)).addExpense(any(Expense.class));
        verify(mockGui, times(1)).updateMonthComboBox();
        assertTrue(result);
    }


    @Test
    void testAddExpenseWithInvalidAmount() {
        boolean result = controller.addExpense("Dinner", "01/04/2023", "Food", "notANumber", "USD");
        verify(mockGui, never()).updateMonthComboBox();
        assertFalse(result);
    }

    @Test
    void testRemoveSelectedExpense() {
        controller.addExpense("Test Expense", "07/09/1981", "Food", "0.0", "USD");
        mockGui.expensesTable.addRowSelectionInterval(0, 0);
        when(mockManager.removeExpense(anyInt())).thenReturn(true);

        try (MockedStatic<JOptionPane> mockedStatic = Mockito.mockStatic(JOptionPane.class)) {
            mockedStatic.when(() -> JOptionPane.showConfirmDialog(
                            any(), anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.YES_OPTION);

            controller.removeSelectedExpense();

            verify(mockManager, times(1)).removeExpense(anyInt());
            verify(mockGui, times(2)).updateMonthComboBox();
        }
    }

    @Test
    void testEditSelectedExpense() throws IOException {
        // Arrange
        mockGui.expensesTable = mock(JTable.class);
        mockGui.monthComboBox = mock(JComboBox.class);

        // Mock GUI interactions
        when(mockGui.expensesTable.getSelectedRow()).thenReturn(0);
        when(mockGui.monthComboBox.getSelectedItem()).thenReturn(YearMonth.now());
        doNothing().when(mockGui).updateMonthComboBox();
        doNothing().when(mockGui).updateTableRow(any(Expense.class), anyInt());
        doNothing().when(mockGui).updateTotalExpensesByCategoryDisplay();

        // Prepare the data
        List<Expense> expenses = new ArrayList<>();
        Expense oldExpense = new Expense("Test Expense", "07/09/1981", "Food", 0.0, "USD");
        expenses.add(oldExpense);
        when(mockManager.getAllExpenses()).thenReturn(expenses);
        when(mockManager.getExpensesGroupedByMonth()).thenReturn(new TreeMap<YearMonth, List<Expense>>() {{
            put(YearMonth.now(), expenses);
        }});

        // Simulate user input for editing an expense
        Expense newExpense = new Expense("Edited Expense", "07/09/1981", "Food", 20.0, "USD");
        when(mockManager.getService()).thenReturn(mockService);
        doNothing().when(mockService).convertExpenseCurrency(any(Expense.class), anyString());

        // Mock the getUpdatedExpenseFromUser method to avoid UI interaction
        ExpenseController controllerSpy = spy(controller);
        doReturn(newExpense).when(controllerSpy).getUpdatedExpenseFromUser(oldExpense);
        when(mockManager.editExpense(any(Expense.class), any(Expense.class))).thenReturn(true);

        // Act
        controllerSpy.editSelectedExpense();

        // Assert
        verify(mockManager, times(1)).editExpense(any(Expense.class), any(Expense.class));
        verify(mockGui, times(1)).updateMonthComboBox();
        verify(mockGui, times(1)).updateTableRow(any(Expense.class), anyInt());
        if (mockGui.totalsPanelVisible) {
            verify(mockGui, times(1)).updateTotalExpensesByCategoryDisplay();
        }
    }


    @Test
    void testConvertExpenseCurrency() throws IOException {
        Expense expense = new Expense("Book", "01/04/2023", "Other", 15.0, "USD");
        mockManager.addExpense(expense);
        doNothing().when(mockService).convertExpenseCurrency(expense, "EUR");
        controller.convertExpenseCurrency(expense, "EUR");
        verify(controller.expenseManager.getService(), times(1)).convertExpenseCurrency(expense, "EUR");
    }
}