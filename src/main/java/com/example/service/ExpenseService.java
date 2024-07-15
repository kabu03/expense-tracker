package com.example.service;

import com.example.model.Expense;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class ExpenseService {

    /**
     * Displays the currency of an expense as a different currency using an API
     * and returns the converted amount, without modifying the expense object.
     *
     * @param expense        the expense to convert
     * @param targetCurrency the new amount returned will be in this currency
     * @return the converted amount
     * @throws IOException if there is an issue with the currency conversion API
     */
    public double displayExpenseAs(Expense expense, String targetCurrency) throws IOException {
        String apiKey = System.getenv("EXCHANGE_RATE_API_KEY"); // Stored in the system's environmental variables.
        if (apiKey == null) {
            throw new IllegalStateException("API key not found in environment variables");
        }
        String apiUrl = String.format("https://v6.exchangerate-api.com/v6/%s/pair/%s/%s",
                apiKey, expense.getCurrency(), targetCurrency);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(apiUrl);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String result = EntityUtils.toString(entity);
            JSONObject json = new JSONObject(result);
            double conversionRate = json.getDouble("conversion_rate");
            return expense.getAmount() * conversionRate;
        }
        return 0; // Return 0 or an appropriate value in case of failure
    }

    /**
     * Converts the currency of an expense to a different currency, modifying the expense object itself.
     *
     * @param expense        the expense to convert
     * @param targetCurrency the new currency to convert to
     * @throws IOException if there is an issue with the currency conversion API
     */
    public void convertExpenseCurrency(Expense expense, String targetCurrency) throws IOException {
        double convertedAmount = displayExpenseAs(expense, targetCurrency);
        if (convertedAmount > 0) { // Check to ensure conversion was successful
            System.out.println("The expense has changed from " + expense.getAmount() + " "
                    + expense.getCurrency() + " to " + convertedAmount + " " + targetCurrency);
            expense.setAmount(convertedAmount);
            expense.setCurrency(targetCurrency);
        }
    }
}
