package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Tramite;
import com.universidad.kardex_backend.model.Estudiante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tramites")
@CrossOrigin(origins = "*")
public class TramiteController {

    @PersistenceContext
    private EntityManager entityManager;

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    /**
     * FASE 1: Almacenamiento Seguro.
     * El estudiante crea un trámite en SU esquema aislado
     */
    @PostMapping("/crear")
    @Transactional
    public ResponseEntity<?> crearTramite(@RequestBody Tramite tramite) {
        try {
            String schema = getCurrentSchema();
            System.out.println("📝 Creando trámite en esquema: " + schema);

            // Guardar en el esquema del estudiante
            String sql = "INSERT INTO " + schema + ".tramites (ru, codigo_seguridad, descripcion) VALUES ('"
                    + tramite.getRu() + "', '"
                    + tramite.getCodigoSeguridad() + "', '"
                    + tramite.getDescripcion() + "') RETURNING id";

            Query query = entityManager.createNativeQuery(sql);

            Number result = (Number) query.getSingleResult();
            Long id = result.longValue();

            return ResponseEntity.ok("Trámite registrado en tu entorno aislado. ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar el trámite: " + e.getMessage());
        }
    }

    /**
     * FASE 2: La Detonación (Vulnerable) - SQL Injection de Segundo Orden
     * El administrador revisa el trámite. El payload se guardó en el esquema del
     * estudiante
     * y ahora se ejecuta en la consulta del administrador.
     */
    @GetMapping("/auditar/{id}")
    public ResponseEntity<?> auditarTramite(@PathVariable("id") Long id) {
        try {
            String schema = getCurrentSchema();

            // 1. Buscar el trámite en el esquema del estudiante
            String findSql = "SELECT * FROM " + schema + ".tramites WHERE id = " + id;
            Query findQuery = entityManager.createNativeQuery(findSql, Tramite.class);
            List<Tramite> tramites = findQuery.getResultList();

            if (tramites.isEmpty()) {
                return ResponseEntity.status(404).body("El trámite solicitado no existe.");
            }

            Tramite tramite = tramites.get(0);

            // 2. VULNERABILIDAD: SQL Injection de Segundo Orden
            // El 'codigoSeguridad' guardado (que pudo ser malicioso) se concatena
            // directamente
            String sql = "SELECT * FROM " + schema + ".estudiantes WHERE carrera = '" + tramite.getCodigoSeguridad()
                    + "'";
            System.out.println("🔍 EJECUTANDO SQL PELIGROSA: " + sql);

            Query query = entityManager.createNativeQuery(sql, Estudiante.class);
            List<Estudiante> resultados = query.getResultList();

            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en SQL Injection de Segundo Orden: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT PARA ADMIN: Ver todos los trámites de todos los estudiantes
     * (Esto requiere consultar todos los esquemas)
     */
    @GetMapping("/admin/listar-todos")
    public ResponseEntity<?> listarTodosTramites() {
        try {
            // Simplificado: usar la tabla public si existe
            String sql = "SELECT * FROM public.tramites";
            List<Tramite> tramites = entityManager.createNativeQuery(sql, Tramite.class).getResultList();
            return ResponseEntity.ok(tramites);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}