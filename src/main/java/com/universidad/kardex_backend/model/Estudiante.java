package com.universidad.kardex_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    private String ru; // Registro Universitario
    private String ci;
    private String nombres;
    private String apellidos;
    private String carrera;

    // Constructores, Getters y Setters
    public Estudiante() {}

    public Estudiante(String ru, String ci, String nombres, String apellidos, String carrera) {
        this.ru = ru;
        this.ci = ci;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.carrera = carrera;
    }
    
    // (Generar Getters y Setters aquí)
    public String getRu() { return ru; }
    public void setRu(String ru) { this.ru = ru; }
    public String getCi() { return ci; }
    public void setCi(String ci) { this.ci = ci; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
}
