package com.example.exchange.service;

import com.example.exchange.util.RedisClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Service responsible for:
 * 1) Fetching latest FX rates from a public API (Frankfurter).
 * 2) Deriving additional currency pairs from the base ones.
 * 3) Caching all pairs in Redis as simple key-value strings.
 *
 * Keys used in Redis:
 *   - USD_EUR, USD_GBP (fetched directly)
 *   - EUR_USD, GBP_USD, EUR_GBP, GBP_EUR (derived)
 */
public class ExchangeRateService {

    // Simple wrapper around Jedis that reads REDIS_HOST from env (defaults to localhost)
    private final RedisClient cache = new RedisClient();

    /**
     * Fetches USD->EUR and USD->GBP, then computes the remaining cross pairs.
     * Stores everything in Redis, one key per pair.
     */
    public void fetchAndStore() {
        // Free endpoint (no API key required)
        // Docs: https://www.frankfurter.app/
        String apiUrl = "https://api.frankfurter.app/latest?from=USD&to=EUR,GBP";

        try {
            // Prepare HTTP GET
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); 

            // Quick debug to confirm the request and status code
            System.out.println("[HTTP] " + conn.getRequestMethod() + " " + url);
            int code = conn.getResponseCode();
            System.out.println("[HTTP] Response code: " + code);
            if (code != 200) {
                // Non-OK often means rate limiting / outage / network issue
                System.err.println("[HTTP] Non-OK status, aborting fetch.");
                return;
            }

            // Read full response body into a string
            String json;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                json = sb.toString();
            }
            // Truncate large payloads for cleaner logs
            System.out.println("[HTTP] Payload: " + (json.length() > 200 ? json.substring(0, 200) + "..." : json));

            // Parse JSON and extract the "rates" object (e.g., {"EUR": 0.86, "GBP": 0.75, ...})
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject rates = root.getAsJsonObject("rates");
            if (rates == null || !rates.has("EUR") || !rates.has("GBP")) {
                // Defensive check: the endpoint normally returns both
                System.err.println("[PARSE] Missing EUR/GBP in response.");
                return;
            }

            // Base rates from USD (as BigDecimal for precise math)
            BigDecimal usdEur = rates.get("EUR").getAsBigDecimal(); // USD -> EUR
            BigDecimal usdGbp = rates.get("GBP").getAsBigDecimal(); // USD -> GBP

            // We’ll compute cross pairs with reasonable precision.
            // Adjust 'scale' if you need more/less decimal places.
            int scale = 6;
            RoundingMode rm = RoundingMode.HALF_UP;

            // Derived pairs:
            // EUR -> USD is the reciprocal of USD -> EUR, etc.
            BigDecimal eurUsd = BigDecimal.ONE.divide(usdEur, scale, rm);      // EUR -> USD
            BigDecimal gbpUsd = BigDecimal.ONE.divide(usdGbp, scale, rm);      // GBP -> USD
            BigDecimal eurGbp = usdEur.divide(usdGbp, scale, rm);               // EUR -> GBP (via USD)
            BigDecimal gbpEur = BigDecimal.ONE.divide(eurGbp, scale, rm);  // GBP->EUR

            save("USD_EUR", usdEur);
            save("USD_GBP", usdGbp);
            save("EUR_USD", eurUsd);
            save("GBP_USD", gbpUsd);
            save("EUR_GBP", eurGbp);
            save("GBP_EUR", gbpEur);

        } catch (IOException ex) {
            System.err.println("[ERROR] fetchAndStore failed: " + ex.getMessage());
        }
    }

    private void save(String key, BigDecimal value) {
        cache.setRate(key, value.toPlainString());
        System.out.println("[CACHE] " + key + "=" + value.toPlainString());
    }

    public String getFromCache(String key) {
        return cache.getRate(key);
    }
}





