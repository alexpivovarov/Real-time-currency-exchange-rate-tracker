// Entry point of the programm

public class ExchangeRateApp {
    public static void main(String[] args) {
        ExchangeRateService service = new ExchangeRateService(); // creates the object that will handle fetching exchange rates.

        Timer timer = new Timer(); // Java's built-in scheduler
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                service.fetchAndCacheRates();
            }
        }, 0, 5 * 60 * 1000); // Run every 5 minutes
        
        System.out.println("Exchange rate tracker started.") // Confirms the app is running
    }
}