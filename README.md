This project is a real-time currency tracker built in **Java 17**, using:
- REST API with **SparkJava**
- **Redis** for in-memory caching
- **Docker** and *Docker Compose**
- Unit tests with **Spock** and **Groovy*

Features
- Fetches live exchange rates from an external API
- Caches them in Redis
- REST endpoints for:
   - '/ping' - check app status
   - '/rate?from=USDto=EUR' - get exchange rate
   - 'convert?from=USD&to=EUR&amount=100' - convert currency
- runs fully in Docker

How to run (with Docker)

  



Fetch and cache real-time exchange rates
REST API with SparkJava
Unit testing with Spock and TDD (test-driven deployment) approach
