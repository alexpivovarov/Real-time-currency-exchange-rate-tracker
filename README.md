# Real-Time Currency Exchange Rate Tracker

A lightweight REST API built with **Java 17 + SparkJava**, using **Redis** for caching and **Docker Compose** for deployment.  
Fetches and serves real-time USD↔EUR and USD↔GBP exchange rates from [Frankfurter.app](https://www.frankfurter.app).

---

## Features
- Fetches **live USD→EUR** and **USD→GBP** rates from Frankfurter.
- **Caches results** in Redis for faster access.
- Automatically **derives cross pairs**:  
  `EUR_USD`, `GBP_USD`, `EUR_GBP`, `GBP_EUR`.
- Runs fully containerized via **Docker Compose**.

---

## Endpoints
| Method | Endpoint | Description |
|--------|-----------|-------------|
| `GET`  | `/health` | Returns `ok` if the service is healthy. |
| `POST` | `/fetch`  | Fetches and caches the latest rates. |
| `GET`  | `/rate/{PAIR}` | Returns the exchange rate (e.g. `USD_EUR`, `EUR_GBP`). |

Example:
```bash
curl -s -X POST http://127.0.0.1:4567/fetch
curl -s http://127.0.0.1:4567/rate/USD_EUR

