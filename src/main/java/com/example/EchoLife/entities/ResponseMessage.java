package com.example.EchoLife.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class ResponseMessage {
    private String status;
    private JsonNode message;

    public ResponseMessage(String status, JsonNode message) {
        this.status = status;
        this.message = message;
    }

    // Getters et setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonNode getMessage() {
        return message;
    }

    public void setMessage(JsonNode message) {
        this.message = message;
    }
}