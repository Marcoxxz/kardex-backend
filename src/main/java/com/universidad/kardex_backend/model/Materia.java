package com.universidad.kardex_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "materias")
public class Materia {

    @Id
    @Column(length = 10)
    private String sigla; // Ej: "INF-101", "MAT-201"

    @Column(nullable = false, length = 100)
    private String nombre; // Ej: "Programación I"

    @Column(nullable = false)
    private Integer creditos; // Ej: 3, 4, 5

    @Column(length = 50)
    private String carrera; // Ej: "Ing. Informática"

    private Integer semestre; // Ej: 1, 2, 3 (semestre sugerido)

    @Column(length = 10)
    private String requisito; // Ej: "INF-101" o "ninguno"

    @Column(length = 20)
    private String area; // Ej: "Programación", "Matemáticas", "Bases de Datos"

    @Column(nullable = false)
    private Boolean activo = true; // Para habilitar/deshabilitar materias

    // Constructor vacío (obligatorio para JPA)
    public Materia() {
    }

    // Constructor para creación rápida
    public Materia(String sigla, String nombre, Integer creditos, String carrera, Integer semestre) {
        this.sigla = sigla;
        this.nombre = nombre;
        this.creditos = creditos;
        this.carrera = carrera;
        this.semestre = semestre;
        this.activo = true;
    }

    // Constructor completo
    public Materia(String sigla, String nombre, Integer creditos, String carrera,
            Integer semestre, String requisito, String area) {
        this.sigla = sigla;
        this.nombre = nombre;
        this.creditos = creditos;
        this.carrera = carrera;
        this.semestre = semestre;
        this.requisito = requisito;
        this.area = area;
        this.activo = true;
    }

    // Getters y Setters
    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public String getRequisito() {
        return requisito;
    }

    public void setRequisito(String requisito) {
        this.requisito = requisito;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}