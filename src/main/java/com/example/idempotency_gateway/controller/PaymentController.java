package com.example.idempotency_gateway.controller;

import com.example.idempotency_gateway.model.PaymentRequest;
import com.example.idempotency_gateway.model.StoredResponse;
import com.example.idempotency_gateway.service.PaymentService;
import com.example.idempotency_gateway.store.IdempotencyStore;  
import com.example.idempotency_gateway.utils.HashUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process-payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final IdempotencyStore store = new IdempotencyStore();

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> process(
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @RequestBody PaymentRequest request) {

        if (key == null) {
            return ResponseEntity.badRequest().body("Idempotency-Key header is required");
        }

        String hash = HashUtil.hash(request);
        IdempotencyStore.Entry existing = store.get(key);

        try {
            if (existing == null) {
                store.create(key, hash);

                var result = paymentService.process(request);
                var response = new StoredResponse(201, result);

                store.complete(key, response);

                return ResponseEntity.status(201).body(result);
            }

            if (!existing.getBodyHash().equals(hash)) {
                return ResponseEntity.status(409)
                        .body("Idempotency key already used for a different request body.");
            }

            if ("processing".equals(existing.getStatus())) {
                var stored = existing.getFuture().get();

                return ResponseEntity.status(stored.getStatusCode())
                        .header("X-Cache-Hit", "true")
                        .body(stored.getBody());
            }

            return ResponseEntity.status(existing.getResponse().getStatusCode())
                    .header("X-Cache-Hit", "true")
                    .body(existing.getResponse().getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error");
        }
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}


