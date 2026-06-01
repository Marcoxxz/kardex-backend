package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.model.Nota;
import com.universidad.kardex_backend.repository.NotaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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

    // Método para obtener el esquema actual
    private String getCurrentSchema() {
        Query query = entityManager.createNativeQuery("SELECT current_schema()");
        return (String) query.getSingleResult();
    }

    @Autowired
    private NotaRepository notaRepository; // Necesitas inyectar el repository

    /**
     * VULNERABILIDAD IDOR (Insecure Direct Object Reference)
     * El endpoint expone la llave primaria directamente en la URL.
     * Cualquier estudiante autenticado puede cambiar el {id} correlativo
     * y acceder a las notas privadas de cualquier otro estudiante.
     */
    @GetMapping("/consultar/{id}")
    public ResponseEntity<?> consultarNotaPorId(@PathVariable("id") Long id) {
        try {
            Nota nota = entityManager.find(Nota.class, id);

            if (nota != null) {
                return ResponseEntity.ok(nota);
            } else {
                return ResponseEntity.status(404).body("Registro de calificación no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    // ============ ENDPOINTS NUEVOS PARA KARDEX ============

    /**
     * Obtener TODAS las notas de un estudiante por su RU
     * VULNERABILIDAD: No valida que el usuario autenticado sea el dueño de las
     * notas
     */
    @GetMapping("/estudiante/{ru}")
    public ResponseEntity<?> getNotasByEstudiante(@PathVariable("ru") String ru) {
        try {
            // VULNERABLE: Cualquier usuario puede ver notas de cualquier RU
            List<Nota> notas = notaRepository.findByRu(ru);

            if (notas.isEmpty()) {
                return ResponseEntity.status(404).body("No se encontraron notas para el estudiante: " + ru);
            }

            return ResponseEntity.ok(notas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    /**
     * Obtener KARDEX COMPLETO con cálculos de promedio
     * Este es el endpoint MÁS IMPORTANTE para tu sistema
     */
    @GetMapping("/kardex/{ru}")
    public ResponseEntity<?> getKardexCompleto(@PathVariable("ru") String ru) {
        try {
            List<Nota> notas = notaRepository.findByRu(ru);

            if (notas.isEmpty()) {
                return ResponseEntity.status(404).body("Estudiante sin historial académico");
            }

            // Calcular estadísticas
            int totalCreditos = 0;
            int sumaPonderada = 0;
            int aprobadas = 0;
            int reprobadas = 0;

            for (Nota nota : notas) {
                int creditos = nota.getCreditos() != null ? nota.getCreditos() : 0;
                int notaFinal = nota.getNotaFinal();

                totalCreditos += creditos;
                sumaPonderada += notaFinal * creditos;

                if (notaFinal >= 51) {
                    aprobadas++;
                } else {
                    reprobadas++;
                }
            }

            double promedioPonderado = totalCreditos > 0 ? (double) sumaPonderada / totalCreditos : 0;

            // Construir respuesta del kardex
            KardexResponse kardex = new KardexResponse(
                    ru,
                    notas,
                    Math.round(promedioPonderado * 100.0) / 100.0,
                    totalCreditos,
                    aprobadas,
                    reprobadas);

            return ResponseEntity.ok(kardex);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al generar kardex: " + e.getMessage());
        }
    }

    /**
     * Registrar nueva nota (solo para administradores/profesores)
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarNota(@RequestBody Nota nota) {
        try {
            // VULNERABLE: No hay validación de datos
            if (nota.getNotaFinal() < 0 || nota.getNotaFinal() > 100) {
                return ResponseEntity.badRequest().body("La nota debe estar entre 0 y 100");
            }

            Nota nuevaNota = notaRepository.save(nota);
            return ResponseEntity.status(201).body(nuevaNota);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar nota: " + e.getMessage());
        }
    }

    /**
     * Actualizar nota existente
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarNota(@PathVariable Long id, @RequestBody Nota notaActualizada) {
        try {
            Nota notaExistente = entityManager.find(Nota.class, id);

            if (notaExistente == null) {
                return ResponseEntity.status(404).body("Nota no encontrada");
            }

            // Actualizar campos permitidos
            notaExistente.setNotaFinal(notaActualizada.getNotaFinal());
            notaExistente.setCreditos(notaActualizada.getCreditos());

            Nota saved = notaRepository.save(notaExistente);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar: " + e.getMessage());
        }
    }

    // Clase interna para respuesta del kardex
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