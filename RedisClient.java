// communicates with Redis

public class RedisClient {
    private final Jedis jedis;

    public RedisClient() {
        this.jedis = new Jedis("localhost", 6379)   // Connects to Redis on your machine (localhost: 6379)
    }

    public void set(String key, String value) {
        jedis.set(key, value);  // Saves key-value pair to Redis
    }

    public String get(String key) {
        return jedis.get(key);  // Fetches a value from Redis by its key
    }
}

// Every 5 minutes, your app calls a currency echange API, gets the latest USD-to-other-currency rates, and saves them into Redis like a key-value store
// Example: 
// "USD_EUR" -> "0.893"
// "USD_GBP" -> "0.7814"