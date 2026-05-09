package com.example.idempotency_gateway.model;

public class StoredResponse {
      private int statusCode;
    private Object body;

    public StoredResponse(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() { return statusCode; }
    public Object getBody() { return body; }
}
