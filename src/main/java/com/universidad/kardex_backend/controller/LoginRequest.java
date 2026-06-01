package com.universidad.kardex_backend.controller;

public class LoginRequest {
    private String ru; // ← NUEVO: para estudiantes
    private String username; // para admin
    private String password;

    // Constructor vacío
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