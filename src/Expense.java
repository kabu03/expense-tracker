import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Expense implements Serializable { // For serialization.
    private String name;
    private String date;
    private String category;
    private double amount;
    private String currency;


    private static final List<String> allowedCategoryNames = Arrays.asList("food", "rent", "groceries", "utilities", "transportation", "entertainment", "other");

    // A predefined category type list.
    // Constructor.
    public Expense(String name, String date, String category, double amount, String currency) {
        this.name = name;
        if (!setDate(date)) {
            throw new IllegalArgumentException("Invalid date format: " + date + ", please use dd/MM/yyyy.");
        }
        setCategory(category);
        this.amount = amount;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
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

    public double displayExpenseAs(String targetCurrency) throws IOException {
        String apiKey = System.getenv("EXCHANGE_RATE_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("API key not found in environment variables");
        }
        String apiUrl = String.format("https://v6.exchangerate-api.com/v6/%s/pair/%s/%s", apiKey, this.currency, targetCurrency);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(apiUrl);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String result = EntityUtils.toString(entity);
            JSONObject json = new JSONObject(result);
            double conversionRate = json.getDouble("conversion_rate");
            return this.amount * conversionRate;
        }
        return 0; // Return 0 or an appropriate value in case of failure
    }
    public void convertExpenseCurrency(String targetCurrency) throws IOException {
        double convertedAmount = displayExpenseAs(targetCurrency);
        if (convertedAmount > 0) { // Check to ensure conversion was successful
            System.out.println("The expense has changed from " + this.amount + " " + this.currency + " to " + convertedAmount + " " + targetCurrency);
            this.amount = convertedAmount;
            this.currency = targetCurrency;
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