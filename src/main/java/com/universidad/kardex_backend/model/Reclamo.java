package com.universidad.kardex_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reclamos")
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ru; // Registro Universitario de quien envía
    private String asunto;
    private String detalle; // <--- Aquí es donde se inyectará el script malicioso (<script>...)

    // Constructor vacío
    public Reclamo() {
    }

    public Reclamo(String ru, String asunto, String detalle) {
        this.ru = ru;
        this.asunto = asunto;
        this.detalle = detalle;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
