// fetching and caching logic 

public class ExchangeRateService {
    private final RedisClient redis;
    private static final String API_URL = "https://api.exchangerate.host/latest?base=USD";  // calling public API

    public ExchangeRateService() {
        this.redis = new RedisClient();     //connects to Redis
    }

    public void fetchAndCacheRates() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("Get");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();   //Parsing the JSON
            JsonObject rates = json.getAsJsonObject("rates");

            for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
                String currency = entry.getKey();   // e.g. "EUR"
                BigDecimal rate = entry.getValue().getAsBigDecimal();   //e.g., 0.893   //Looping over currency rates
                redis.set("USD_" + currency, rate.toString());  // Storing each pair to Redis

            }
            System.out.println("Rates updated.");
        } catch (Exception e) {
            e.printStackTrace();

    }


}

