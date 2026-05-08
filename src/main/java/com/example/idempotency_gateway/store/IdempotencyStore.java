package com.example.idempotency_gateway.store;

import com.example.idempotency_gateway.model.StoredResponse;

import java.util.concurrent.*;

public class IdempotencyStore {
    public static class Entry {
        private String bodyHash;
        private String status;
        private long expiry;
        private CompletableFuture<StoredResponse> future;
        private StoredResponse response;

        public String getBodyHash() {
            return bodyHash;
        }

        public String getStatus() {
            return status;
        }

        public CompletableFuture<StoredResponse> getFuture() {
            return future;
        }

        public StoredResponse getResponse() {
            return response;
        }
    }
    
// In-memory store with TTL expiration
    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();
    private final long TTL = 24 * 60 * 60 * 1000;

    public Entry get(String key) {
        Entry entry = store.get(key);
        if (entry != null && entry.expiry < System.currentTimeMillis()) {
            store.remove(key);
            return null;
        }
        return entry;
    }

    public Entry create(String key, String bodyHash) {
        Entry entry = new Entry();
        entry.bodyHash = bodyHash;
        entry.status = "processing";
        entry.expiry = System.currentTimeMillis() + TTL;
        entry.future = new CompletableFuture<>();

        store.put(key, entry);
        return entry;
    }
// Mark the entry as completed with the response
    public void complete(String key, StoredResponse response) {
        Entry entry = store.get(key);
        if (entry != null) {
            entry.status = "done";
            entry.response = response;
            entry.future.complete(response);
        }
    }
}




