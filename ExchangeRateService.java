// fetching and caching logic 
package com.example.exchange.service;

import com.example.exchange.util.RedisClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ExchangeRateService {

    private final RedisClient cache = new RedisClient();

    public void fetchAndStore() {
        // NOTE: Replace with your real endpoint + API key if needed.
        // For demo, we use a free endpoint that returns a tiny JSON.
        String apiUrl = "https://api.frankfurter.app/latest?from=USD&to=EUR,GBP";;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            System.out.println("[HTTP] " + conn.getRequestMethod() + " " + url);
            int code = conn.getResponseCode();
            System.out.println("[HTTP] Response code: " + code);

            if (code != 200) {
                System.err.println("[HTTP] Non-OK status, aborting fetch.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                System.out.println("[HTTP] Payload: " + (json.length() > 200 ? json.substring(0, 200) + "..." : json));

                JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                JsonObject rates = root.getAsJsonObject("rates");
                if (rates != null) {
                    if (rates.has("EUR")) {
                        String v = rates.get("EUR").getAsString();
                        cache.setRate("USD_EUR", v);
                        System.out.println("[CACHE] USD_EUR=" + v);
                    }
                    if (rates.has("GBP")) {
                        String v = rates.get("GBP").getAsString();
                        cache.setRate("USD_GBP", v);
                        System.out.println("[CACHE] USD_GBP=" + v);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("[ERROR] fetchAndStore failed: " + ex.getMessage());
        }
    }

    public String getFromCache(String key) {
        return cache.getRate(key);
    }
}




