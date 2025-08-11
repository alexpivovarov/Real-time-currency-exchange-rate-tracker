package com.example.exchange;

import com.example.exchange.service.ExchangeRateService;
import static spark.Spark.*;

public class ExchangeRateApp {
    public static void main(String[] args) {
        port(4567);

        ExchangeRateService service = new ExchangeRateService();

        // Health
        get("/health", (req, res) -> "ok");

        // Trigger fetch (manual)
        post("/fetch", (req, res) -> {
            service.fetchAndStore();
            return "fetched";
        });

        // Read a rate by key, e.g. USD_EUR
        get("/rate/:pair", (req, res) -> {
            String pair = req.params(":pair");
            String value = service.getFromCache(pair);
            if (value == null) {
                res.status(404);
                return "not found";
            }
            return value;
        });

        System.out.println("[APP] Started on http://localhost:4567");
    }
}



