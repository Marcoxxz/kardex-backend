package com.universidad.kardex_backend.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @PersistenceContext
    private EntityManager entityManager;

    // ============================================
    // RESETEAR ESQUEMA DE UN ESTUDIANTE ESPECÍFICO
    // ============================================
    @PostMapping("/reset-estudiante/{ru}")
    public ResponseEntity<?> resetearEstudiante(@PathVariable String ru) {
        try {
            String resetSql = "SELECT resetear_esquema_estudiante('" + ru + "')";
            Query query = entityManager.createNativeQuery(resetSql);
            String resultado = (String) query.getSingleResult();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Esquema del estudiante " + ru + " reiniciado correctamente");
            response.put("resultado", resultado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================
    // RESETEAR TODOS LOS ESQUEMAS
    // ============================================
    @PostMapping("/reset-all")
    public ResponseEntity<?> resetearTodos() {
        try {
            String resetSql = "SELECT resetear_todos_esquemas()";
            Query query = entityManager.createNativeQuery(resetSql);
            String resultado = (String) query.getSingleResult();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todos los esquemas han sido reiniciados");
            response.put("resultado", resultado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================
    // LISTAR ESTUDIANTES DE PRÁCTICA
    // ============================================
    @GetMapping("/estudiantes")
    public ResponseEntity<?> listarEstudiantesPractica() {
        try {
            String sql = "SELECT ru, nombres, apellidos, email, esquema_creado, fecha_creacion, ultimo_acceso FROM estudiantes_practica";
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> resultados = query.getResultList();
            
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================
    // VER ESTADO DE UN ESQUEMA
    // ============================================
    @GetMapping("/esquema/{ru}")
    public ResponseEntity<?> verEsquema(@PathVariable String ru) {
        try {
            String sql = "SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = 'estudiante_" + ru + "')";
            Query query = entityManager.createNativeQuery(sql);
            Boolean existe = (Boolean) query.getSingleResult();
            
            Map<String, Object> response = new HashMap<>();
            response.put("ru", ru);
            response.put("esquema_existe", existe);
            response.put("esquema_nombre", "estudiante_" + ru);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}