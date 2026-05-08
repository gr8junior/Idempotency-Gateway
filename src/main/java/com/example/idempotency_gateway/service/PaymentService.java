package com.example.idempotency_gateway.service;
import org.springframework.stereotype.Service;

import com.example.idempotency_gateway.model.PaymentRequest;  

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
public class PaymentService {
     public Map<String, Object> process(PaymentRequest request) throws InterruptedException {
        Thread.sleep(2000); // simulate delay

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Charged " + request.getAmount() + " " + request.getCurrency());
        result.put("amount", request.getAmount());
        result.put("currency", request.getCurrency());
        result.put("processedAt", Instant.now().toString());

        return result;
    }
}
