Real-Time Currency Exchange Rate Tracker

Java 17 + SparkJava REST API, Redis cache, Docker & Docker Compose. Tests with Spock (Groovy).  

What it does
 • Fetches live USD→EUR & USD→GBP from Frankfurter and caches them in Redis.
 • Derives cross pairs: EUR_USD, GBP_USD, EUR_GBP, GBP_EUR.
 • Runs fully in Docker/Compose.  

Endpoints
 • GET  /health → ok
 • POST /fetch → fetch & cache latest rates
 • GET  /rate/{PAIR} → returns a rate as text
Examples: USD_EUR, USD_GBP, EUR_USD, GBP_USD, EUR_GBP, GBP_EUR

We query https://api.frankfurter.app/latest?from=USD&to=EUR,GBP once; GBP pairs are derived from those two base rates.

Quick start (Docker)

docker compose up -d --build
curl.exe -s http://127.0.0.1:4567/health
curl.exe -s -X POST http://127.0.0.1:4567/fetch
curl.exe -s http://127.0.0.1:4567/rate/USD_EUR

Tip (Windows): use curl.exe. PowerShell’s curl alias doesn’t support -X POST.

Config
 • REDIS_HOST (set to redis in docker-compose.yml)

Tests (optional)

Spock/Groovy tests are included; run locally with:

gradle test

(Docker build skips tests by default.)  
