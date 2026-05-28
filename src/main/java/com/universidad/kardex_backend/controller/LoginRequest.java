package com.universidad.kardex_backend.controller;

public class LoginRequest {
    private String username;
    private String password;

    // Constructor vacío obligatorio para que Jackson pueda instanciar el objeto
    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters explícitos
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
