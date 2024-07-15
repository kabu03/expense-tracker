package com.example.view;

import com.example.controller.ExpenseController;
import com.example.model.ExpenseManager;
import com.example.service.ExpenseService;
import com.example.utils.ExpenseFileHandler;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        darkMode();
        ExpenseService expenseService = new ExpenseService();
        ExpenseFileHandler expenseFileHandler = new ExpenseFileHandler();
        ExpenseManager expenseManager = new ExpenseManager(expenseService, expenseFileHandler);
        ExpenseController controller = new ExpenseController(expenseManager);
        GUI gui = new GUI(controller);
        controller.setGui(gui);
    }

    // A method to improve the aesthetics of the GUI.
    public static void darkMode() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to the default look and feel.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }

        // Backgrounds: Black and Dark Blues
        UIManager.put("control", new Color(0, 21, 41)); // Control background
        UIManager.put("info", new Color(0, 21, 41)); // Info background
        UIManager.put("nimbusBase", new Color(3, 18, 33)); // Primary color for the nimbus look and feel
        UIManager.put("nimbusLightBackground", new Color(4, 24, 44)); // Light background color
        // UIManager.put("nimbusSelectionBackground", new Color(255, 255, 255)); // Background color when an item is selected
        UIManager.put("Table.background", new Color(4, 24, 44)); // Background color for tables
        UIManager.put("Table.gridColor", new Color(0, 10, 20)); // Grid color for tables
        UIManager.put("TableHeader.background", new Color(0, 21, 41)); // Background color for table headers
        UIManager.put("ScrollPane.background", new Color(0, 21, 41));
        UIManager.put("Viewport.background", new Color(0, 21, 41));
        UIManager.put("TextField.background", new Color(4, 24, 44));
        UIManager.put("PasswordField.background", new Color(4, 24, 44));
        UIManager.put("TextArea.background", new Color(4, 24, 44));
        UIManager.put("Button.background", new Color(3, 18, 33));
        UIManager.put("Menu.background", new Color(0, 21, 41));
        UIManager.put("MenuItem.background", new Color(0, 21, 41));
        UIManager.put("MenuBar.background", new Color(0, 31, 51));

        // Foregrounds and Texts: Lighter Blues
        UIManager.put("nimbusDisabledText", new Color(88, 124, 153)); // Disabled text color
        UIManager.put("nimbusFocus", new Color(0, 82, 155)); // Focus color
        UIManager.put("nimbusSelectedText", new Color(255, 255, 255)); // Text color when an item is selected
        UIManager.put("text", new Color(255, 255, 255)); // Text color
        UIManager.put("Table.foreground", new Color(173, 216, 230)); // Text color for tables
        UIManager.put("TableHeader.foreground", new Color(173, 216, 230)); // Text color for table headers
        UIManager.put("TextField.foreground", new Color(173, 216, 230));
        UIManager.put("TextField.caretForeground", new Color(173, 216, 230));
        UIManager.put("PasswordField.foreground", new Color(173, 216, 230));
        UIManager.put("PasswordField.caretForeground", new Color(173, 216, 230));
        UIManager.put("TextArea.foreground", new Color(173, 216, 230));
        UIManager.put("Button.foreground", new Color(173, 216, 230));
        UIManager.put("Label.foreground", new Color(173, 216, 230));
        UIManager.put("Menu.foreground", new Color(173, 216, 230));
        UIManager.put("MenuItem.foreground", new Color(173, 216, 230));
    }
}