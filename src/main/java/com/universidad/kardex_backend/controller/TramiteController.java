package com.universidad.kardex_backend.controller;

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

    /**
     * FASE 1: Almacenamiento Seguro.
     * El estudiante crea un trámite. Usamos 'entityManager.persist()', lo que
     * significa
     * que cualquier comilla o payload malicioso se guardará LITERALMENTE en la base
     * de datos
     * sin alterar el sistema de forma inmediata.
     */
    @PostMapping("/crear")
    @Transactional
    public ResponseEntity<?> crearTramite(@RequestBody Tramite tramite) {
        try {
            entityManager.persist(tramite);
            return ResponseEntity.ok("Trámite registrado de forma segura. ID de Ticket asignado: " + tramite.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar el trámite: " + e.getMessage());
        }
    }

    /**
     * FASE 2: La Detonación (Vulnerable).
     * Un administrador revisa el trámite mediante su ID. El sistema recupera el
     * 'codigoSeguridad'
     * que estaba guardado e ingenuamente lo concatena en una consulta nativa para
     * filtrar estudiantes.
     */
    @GetMapping("/auditar/{id}")
    public ResponseEntity<?> auditarTramite(@PathVariable("id") Long id) {
        try {
            // 1. Buscamos el trámite de la base de datos de manera limpia
            Tramite tramite = entityManager.find(Tramite.class, id);
            if (tramite == null) {
                return ResponseEntity.status(404).body("El trámite solicitado no existe.");
            }

            // 2. ERROR GRAVE: Tomar un dato de la base de datos y confiar en él ciegamente
            // concatenándolo.
            // Si el 'codigoSeguridad' guardado previamente fue: CualquierCosa' OR '1'='1
            // La consulta se romperá lógicamente trayendo TODOS los estudiantes
            // registrados.
            String sql = "SELECT * FROM estudiantes WHERE carrera = '" + tramite.getCodigoSeguridad() + "'";

            Query query = entityManager.createNativeQuery(sql, Estudiante.class);
            List<Estudiante> resultados = query.getResultList();

            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            // Permite ver errores de sintaxis SQL en caso de que manden comillas impares
            return ResponseEntity.status(500).body("Error interno en el detonador de Segundo Orden: " + e.getMessage());
        }
    }
}