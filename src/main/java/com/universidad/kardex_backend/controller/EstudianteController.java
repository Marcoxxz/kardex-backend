package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Estudiante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estudiantes")
@CrossOrigin(origins = "*") // Permite la conexión desde Vercel
public class EstudianteController {

    @PersistenceContext
    private EntityManager entityManager;

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    // ENDPOINT VULNERABLE: Permite inyección SQL basada en UNION o Ciega
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorCi(@RequestParam("ci") String ci) {
        try {
            String schema = getCurrentSchema();

            // Usar el esquema del estudiante
            String sql = "SELECT ru, apellidos, carrera, ci, nombres FROM " + schema + ".estudiantes WHERE ci = '" + ci
                    + "'";
            Query query = entityManager.createNativeQuery(sql, Estudiante.class);
            List<Estudiante> resultados = query.getResultList();

            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la consulta: " + e.getMessage());
        }
    }
}
