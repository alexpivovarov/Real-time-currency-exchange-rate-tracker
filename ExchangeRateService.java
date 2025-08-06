// fetching and caching logic 

package com.example.exchange.service;

import com.example.exchange.util.RedisClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL
import java.net.Map

public class ExchangeRateService {
    private final RedisClient redis;

    // API URL to fetch exchange rates (base currency: USD)
    private static final String API_URL = "https://api.exchangerate.host/latest?base=USD";  // calling public API

    public ExchangeRateService() {
        this.redis = new RedisClient();     //connects to Redis
    }

    // Fetch rates from API and store them in Redis
    public void fetchAndCacheRates() {
        try {
            // Open connection to API
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("Get");

            // Parse the Json response using Gson
            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();   //Parsing the JSON
            JsonObject rates = json.getAsJsonObject("rates");

            // Store each rate in Redis with key: USD_<currency>
            for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
                String currency = entry.getKey();   // e.g. "EUR"
                BigDecimal rate = entry.getValue().getAsBigDecimal();   //e.g., 0.893   //Looping over currency rates
                redis.set("USD_" + currency, rate.toString());  // Storing each pair to Redis

            }

            System.out.println("Exchange rates updated and cached in Redis.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the exchange rate from Redis
    public String getRate(String from, String to) {
        String key = from.Uppercase() + "_" + to.UpperCase(); // e.g., USD_EUR
        return redis.get(key); // Return the stored rate
    }

    // Convert an amount from one currency to another using stored rates
    public double convert(String from, String to, double amount) {
        String rateStr = getRate(from, to);
        if (rateStr == null) return -1 // Return -1 if rate not found

        double rate = Double.parseDouble(rateStr);
        return amount * rate; // Return converted value
    }


}


