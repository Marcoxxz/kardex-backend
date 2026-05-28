package com.universidad.kardex_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tramites")
public class Tramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ru;
    private String codigoSeguridad; // <--- Aquí los alumnos guardarán el Payload malicioso
    private String descripcion;

    // Constructor vacío
    public Tramite() {
    }

    public Tramite(String ru, String codigoSeguridad, String descripcion) {
        this.ru = ru;
        this.codigoSeguridad = codigoSeguridad;
        this.descripcion = descripcion;
    }

    // Getters y Setters indispensables
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

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
