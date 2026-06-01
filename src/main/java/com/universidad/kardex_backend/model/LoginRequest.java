package com.universidad.kardex_backend.model;

public class LoginRequest {
    private String ru;
    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String ru, String username, String password) {
        this.ru = ru;
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}