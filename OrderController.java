package com.example.fx.orders;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Map<String, Map<String, Object>> idempotencyStore = new ConcurrentHashMap<>();

    private final WebClient.Builder webClientBuilder;

    public OrderController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestHeader(name = "X-Idempotency-Key", required = false) String idemKey,
            @RequestBody OrderRequest req
    ) {
        // Check idempotency
        if (idemKey != null && idempotencyStore.containsKey(idemKey)) {
            return ResponseEntity.status(HttpStatus.OK).body(idempotencyStore.get(idemKey));
        }

        WebClient webClient = webClientBuilder.build(); 

        // Call quote-servcie
        Map<String, Object> quoteResponse = webClient.get()
            .uri("http://quote-service:8081/quotes?base={base}&counter={counter}&amount={amount}",
                req.base(), req.counter(), req.amount())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
                    
        System.out.println("DEBUG >>> Quote response: " + quoteResponse);

        String id = "ord_" + Long.toHexString(System.nanoTime());
        Map<String, Object> payload = Map.of(
                "id", id,
                "base", req.base(),
                "counter", req.counter(),
                "amount", req.amount(),
                "createdAt", Instant.now().toString(),
                "status", "CREATED",
                "rate", quoteResponse.get("mid")
        );

        // Save for idempotency
        if (idemKey != null) {
            idempotencyStore.put(idemKey, payload);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(payload);
    }

    public record OrderRequest(String base, String counter, BigDecimal amount) {}
}
