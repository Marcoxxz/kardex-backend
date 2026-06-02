package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Nota;
import com.universidad.kardex_backend.repository.NotaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notas")
@CrossOrigin(origins = "*")
public class NotaController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NotaRepository notaRepository;

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        System.out.println("SCHEMA ACTUAL = " + schema);
        return schema != null ? schema : "public";
    }

    @GetMapping("/consultar/{id}")
    public ResponseEntity<?> consultarNotaPorId(@PathVariable("id") Long id) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".notas WHERE id = " + id;
            Nota nota = (Nota) entityManager.createNativeQuery(sql, Nota.class).getSingleResult();

            if (nota != null) {
                return ResponseEntity.ok(nota);
            } else {
                return ResponseEntity.status(404).body("Registro no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/estudiante/{ru}")
    public ResponseEntity<?> getNotasByEstudiante(@PathVariable("ru") String ru) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".notas WHERE ru = '" + ru + "'";
            List<Nota> notas = entityManager.createNativeQuery(sql, Nota.class).getResultList();

            if (notas.isEmpty()) {
                return ResponseEntity.status(404).body("No se encontraron notas");
            }
            return ResponseEntity.ok(notas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/kardex/{ru}")
    public ResponseEntity<?> getKardexCompleto(@PathVariable("ru") String ru) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".notas WHERE ru = '" + ru + "'";
            List<Nota> notas = entityManager.createNativeQuery(sql, Nota.class).getResultList();

            if (notas.isEmpty()) {
                return ResponseEntity.status(404).body("Estudiante sin historial académico");
            }

            int totalCreditos = 0;
            int sumaPonderada = 0;
            int aprobadas = 0;
            int reprobadas = 0;

            for (Nota nota : notas) {
                int creditos = nota.getCreditos() != null ? nota.getCreditos() : 0;
                int notaFinal = nota.getNotaFinal();
                totalCreditos += creditos;
                sumaPonderada += notaFinal * creditos;
                if (notaFinal >= 51)
                    aprobadas++;
                else
                    reprobadas++;
            }

            double promedioPonderado = totalCreditos > 0 ? (double) sumaPonderada / totalCreditos : 0;

            KardexResponse kardex = new KardexResponse(ru, notas,
                    Math.round(promedioPonderado * 100.0) / 100.0,
                    totalCreditos, aprobadas, reprobadas);

            return ResponseEntity.ok(kardex);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarNota(@RequestBody Nota nota) {
        try {
            String schema = getCurrentSchema();
            if (nota.getNotaFinal() < 0 || nota.getNotaFinal() > 100) {
                return ResponseEntity.badRequest().body("La nota debe estar entre 0 y 100");
            }

            String sql = "INSERT INTO " + schema
                    + ".notas (ru, sigla_materia, nombre_materia, nota_final, gestion, creditos) VALUES ('"
                    + nota.getRu() + "', '" + nota.getSiglaMateria() + "', '" + nota.getNombreMateria() + "', "
                    + nota.getNotaFinal() + ", '" + nota.getGestion() + "', " + nota.getCreditos() + ") RETURNING *";

            Nota nuevaNota = (Nota) entityManager.createNativeQuery(sql, Nota.class).getSingleResult();
            return ResponseEntity.status(201).body(nuevaNota);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    static class KardexResponse {
        public String ru;
        public List<Nota> materias;
        public double promedioPonderado;
        public int totalCreditos;
        public int materiasAprobadas;
        public int materiasReprobadas;

        public KardexResponse(String ru, List<Nota> materias, double promedioPonderado,
                int totalCreditos, int materiasAprobadas, int materiasReprobadas) {
            this.ru = ru;
            this.materias = materias;
            this.promedioPonderado = promedioPonderado;
            this.totalCreditos = totalCreditos;
            this.materiasAprobadas = materiasAprobadas;
            this.materiasReprobadas = materiasReprobadas;
        }
    }
}