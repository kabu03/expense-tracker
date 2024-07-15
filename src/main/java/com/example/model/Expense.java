// This class represents a single expense entry.

package com.example.model;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Expense implements Serializable { // To allow for serialization.
    private String name;
    private String date;
    private String category;
    private double amount;
    private String currency;

    private static final List<String> allowedCategoryNames = Arrays.asList("food", "rent", "groceries", "utilities", "transportation", "entertainment", "other");

    // A predefined category type list.
    public Expense(String name, String date, String category, double amount, String currency) {
        setName(name);
        if (!setDate(date)) {
            throw new IllegalArgumentException("Invalid date format: " + date + ", please use dd/MM/yyyy.");
        }
        setCategory(category);
        setAmount(amount);
        setCurrency(currency);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // I used the setter methods in the constructor to make sure the date and category inputs are valid.
    public void setCategory(String category) {
        if (category != null && allowedCategoryNames.contains(category.toLowerCase())) {
            this.category = category;
        } else {
            throw new IllegalArgumentException("Invalid category");
        }
    }

    // Typical getters and setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }


    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public boolean setDate(String dateString) {
        if (isValidDate(dateString)) {
            this.date = dateString;
            return true;
        } else {
            return false;
        }
    }

    // Method to validate dates. All expense dates must be in dd/MM/yyyy.
    public boolean isValidDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            formatter.parse(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    // Equals and hashCode methods for proper comparison.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.amount, amount) == 0 && date.equals(expense.date) && category.equals(expense.category) && name.equals(expense.name) && currency.equals(expense.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, category, name, amount, currency);
    }
}