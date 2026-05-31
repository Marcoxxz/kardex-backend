package com.universidad.kardex_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notas")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ru; // Registro Universitario del estudiante
    private String siglaMateria;
    private String nombreMateria;
    private Integer notaFinal;
    private String gestion;
    private Integer creditos; // ← CAMPO NUEVO

    // Constructor vacío
    public Nota() {
    }

    // Constructor actualizado con créditos
    public Nota(String ru, String siglaMateria, String nombreMateria,
            Integer notaFinal, String gestion, Integer creditos) {
        this.ru = ru;
        this.siglaMateria = siglaMateria;
        this.nombreMateria = nombreMateria;
        this.notaFinal = notaFinal;
        this.gestion = gestion;
        this.creditos = creditos;
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

    public String getSiglaMateria() {
        return siglaMateria;
    }

    public void setSiglaMateria(String siglaMateria) {
        this.siglaMateria = siglaMateria;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public Integer getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Integer notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getGestion() {
        return gestion;
    }

    public void setGestion(String gestion) {
        this.gestion = gestion;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }
}