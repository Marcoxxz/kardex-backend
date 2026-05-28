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

    private String ru; // Registro Universitario del estudiante dueño de la nota
    private String siglaMateria;
    private String nombreMateria;
    private Integer notaFinal;
    private String gestion;

    // Constructor vacío
    public Nota() {
    }

    public Nota(String ru, String siglaMateria, String nombreMateria, Integer notaFinal, String gestion) {
        this.ru = ru;
        this.siglaMateria = siglaMateria;
        this.nombreMateria = nombreMateria;
        this.notaFinal = notaFinal;
        this.gestion = gestion;
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
}
