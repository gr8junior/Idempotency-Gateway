package com.example.idempotency_gateway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;

public class HashUtil {
     private static final ObjectMapper mapper = new ObjectMapper();

    public static String hash(Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(json.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}