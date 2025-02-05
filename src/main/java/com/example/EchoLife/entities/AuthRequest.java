package com.example.EchoLife.entities;

public class AuthRequest {
    private String email;
    private String password;

    // Constructeurs
    public AuthRequest() {
    }

    public AuthRequest(String username, String password) {
        this.email = username;
        this.password = password;
    }

    // Getters et setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
