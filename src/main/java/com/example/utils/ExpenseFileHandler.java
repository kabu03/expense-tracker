package com.example.utils;

import com.example.model.Expense;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExpenseFileHandler {
    private final String fileName;

    public ExpenseFileHandler() {
        // Setting the relative path for my file.
        fileName = "expenses.ser";
    }

    // Another constructor that takes a fileName as a parameter, which may prove useful for testing.
    public ExpenseFileHandler(String fileName) {
        this.fileName = fileName;
    }

    public void saveExpensesToFile(List<Expense> expenses) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            oos.writeObject(expenses);
        }
    }

    public List<Expense> loadExpensesFromFile() throws IOException, ClassNotFoundException {
        List<Expense> expenses;
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            expenses = (List<Expense>) ois.readObject();
        }
        return expenses;
    }
}