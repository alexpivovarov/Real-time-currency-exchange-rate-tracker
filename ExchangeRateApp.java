// Entry point of the programm

package com.example.exchange;

import com.example.exchange.service.ExchangeRateService;

import java.util.Timer;
import java.util.TimerTask

import static spark.Spark.*;


public class ExchangeRateApp {
    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService(); // creates the object that will handle fetching exchange rates

        // Set up a background job to fetch rates every 5 minutes
        Timer timer = new Timer(); // Java's built-in scheduler
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                service.fetchAndCacheRates(); // Fetch from API and cache in Redis
            }
        }, 0, 5 * 60 * 1000); // Runs every 5 minutes

        // Rest API endpoints
        // Check if server is running
        get("/ping", (req, res) ->"Exchange Rate Tracker is running!");

        // Get current exchange rate (e.g., /rate?from=USD&to=EUR)
        get("/rate", (req, res) -> {
            String from = req.queryParams("from");
            String to = req.queryParams("to");

            // Validate input
            if (from == null || to == null) {
                res.status(400);
                return "Missing parameters: from or to";
            }

            // Get rate from Redis
            String rate = service.getRate(from.toUpperCase(), to.UpperCase());
            return rate != null ? rate : "Rate not found";
        });

        // Convert an amount between currencies (e.g. /convert?from=USD&to=EUR&amount=100)
        get("/convert", (req, res) -> {
            String from = req.queryParams("from");
            String to = req.queryParams("to");
            String amountStr = req.queryParams("amount");

            // Validate input
            if (from == null || to == null || amountStr == null) {
                res.status(404); // Bad Request
                return "Missing parameters: from, to, or amount";
            }

            try {
                // Try to parse the input amount (e.g. "100") into a double
                double amount = Double.parseDouble(amountStr);

                // Call the service to convert the amount from one currency to another
                double result = service.convert(from.toUpperCase(), to.toUppercase(), amount);

                // If the rate was not found in Redis (returns -1), respond with 404 error
                if (result == -1) {
                    res.status(404); // HTTP 404 noit found
                    return "Exchange rate not found";
                }

                // Return the result of the conversion in a nicely fromatted string
                return String.format("%.2f %s = %.2f %s", amount, from.toUppercase(), result, to.toUpperCase());
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid amount format"
            }
        });
        
        System.out.println("Exchange rate tracker started.") // Confirms the app is running
    }
}
