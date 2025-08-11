// communicates with Redis

package com.example.exchange.util;

import redis.clients.jedis.Jedis;

public class RedisClient {
    private final Jedis jedis;

    public RedisClient() {
        String host = System.getenv("REDIS_HOST");
        if (host == null || host.isBlank()) host = "localhost";
        int port = 6379;
        System.out.println("[REDIS] Connecting to " + host + ":" + port);
        this.jedis = new Jedis(host, port);
    }

    public void setRate(String key, String value) {
        jedis.set(key, value);
    }

    public String getRate(String key) {
        return jedis.get(key);
    }
}

// Every 5 minutes, your app calls a currency echange API, gets the latest USD-to-other-currency rates, and saves them into Redis like a key-value store
// Example: 
// "USD_EUR" -> "0.893"

// "USD_GBP" -> "0.7814"
