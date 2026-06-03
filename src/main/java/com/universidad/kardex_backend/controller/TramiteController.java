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
     * FASE 1: Almacenamiento Seguro (Blindado contra Inyección de Primer Orden).
     * Guardamos el payload usando parámetros (?, ?), simulando que la app almacena
     * el dato de forma "segura" en el entorno aislado del estudiante.
     */
    @PostMapping("/crear")
    @Transactional
    public ResponseEntity<?> crearTramite(@RequestBody Tramite tramite) {
        try {
            String schema = getCurrentSchema();
            System.out.println("📝 Almacenando trámite en esquema seguro: " + schema);

            // Usamos parámetros nombrados para EVITAR SQLi de Primer Orden aquí.
            // El esquema se concatena dinámicamente porque los nombres de tablas/esquemas
            // no aceptan parámetros.
            String sql = "INSERT INTO " + schema
                    + ".tramites (ru, codigo_seguridad, descripcion) VALUES (:ru, :codigoSeguridad, :descripcion) RETURNING id";

            Query query = entityManager.createNativeQuery(sql)
                    .setParameter("ru", tramite.getRu())
                    .setParameter("codigoSeguridad", tramite.getCodigoSeguridad())
                    .setParameter("descripcion", tramite.getDescripcion());

            Number result = (Number) query.getSingleResult();
            Long id = result.longValue();

            return ResponseEntity.ok("Trámite registrado de forma segura en almacenamiento. ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar el trámite: " + e.getMessage());
        }
    }

    /**
     * FASE 2: La Detonación (Vulnerable) - SQL Injection de Segundo Orden.
     * El payload previamente almacenado se recupera de la base de datos y se
     * concatena
     * directamente en una nueva consulta de auditoría, alterando la lógica del
     * negocio.
     */
    @GetMapping("/auditar/{id}")
    @Transactional(readOnly = true) // Agregamos readOnly para proteger el estado transaccional si hay excepciones
                                    // SQL
    public ResponseEntity<?> auditarTramite(@PathVariable("id") Long id) {
        try {
            String schema = getCurrentSchema();
            System.out.println("🔍 Auditoría solicitada en SCHEMA: " + schema + " para ID: " + id);

            // 1. Recuperamos el trámite de la BD (Uso de parámetro para evitar inyecciones
            // previas)
            String findSql = "SELECT * FROM " + schema + ".tramites WHERE id = :id";
            Query findQuery = entityManager.createNativeQuery(findSql, Tramite.class)
                    .setParameter("id", id);

            List<Tramite> tramites = findQuery.getResultList();

            if (tramites.isEmpty()) {
                return ResponseEntity.status(404).body("El trámite solicitado no existe.");
            }

            Tramite tramite = tramites.get(0);

            // 2. VULNERABILIDAD: El payload recuperado se concatena crudo en la lógica de
            // negocio.
            String sql = "SELECT * FROM " + schema + ".estudiantes WHERE carrera = '" + tramite.getCodigoSeguridad()
                    + "'";
            System.out.println("💥 DETONANDO SQL DE SEGUNDO ORDEN: " + sql);

            Query query = entityManager.createNativeQuery(sql, Estudiante.class);
            List<Estudiante> resultados = query.getResultList();

            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error en la ejecución del payload (SQLi 2do Orden): " + e.getMessage());
        }
    }

    @GetMapping("/admin/listar-todos")
    public ResponseEntity<?> listarTodosTramites() {
        try {
            String sql = "SELECT * FROM public.tramites";
            List<Tramite> tramites = entityManager.createNativeQuery(sql, Tramite.class).getResultList();
            return ResponseEntity.ok(tramites);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}