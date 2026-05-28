package com.universidad.kardex_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password; // Aquí se guardará el hash MD5
    private String nombreReal;
    private String rol;

    // Constructor vacío obligatorio para JPA
    public Usuario() {}

    public Usuario(String username, String password, String nombreReal, String rol) {
        this.username = username;
        this.password = password;
        this.nombreReal = nombreReal;
        this.rol = rol;
    }

    // Getters y Setters necesarios para la serialización JSON
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreReal() { return nombreReal; }
    public void setNombreReal(String nombreReal) { this.nombreReal = nombreReal; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
