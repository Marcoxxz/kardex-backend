package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Reclamo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reclamos")
@CrossOrigin(origins = "*")
public class ReclamoController {

    @PersistenceContext
    private EntityManager entityManager;

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    /**
     * ENDPOINT 1: Enviar Reclamo
     * Guarda el reclamo en el ESQUEMA del estudiante (aislado)
     */
    @PostMapping("/enviar")
    @Transactional
    public ResponseEntity<?> enviarReclamo(@RequestBody Reclamo reclamo) {
        try {
            String schema = getCurrentSchema();
            System.out.println("📝 Guardando reclamo en esquema: " + schema);

            // Usar nativeQuery con el esquema correcto
            String sql = "INSERT INTO " + schema + ".reclamos (ru, asunto, detalle, fecha) VALUES ('"
                    + reclamo.getRu() + "', '"
                    + reclamo.getAsunto() + "', '"
                    + reclamo.getDetalle() + "', CURRENT_TIMESTAMP)";

            entityManager.createNativeQuery(sql).executeUpdate();

            return ResponseEntity.ok("Reclamo enviado exitosamente a tu entorno de prácticas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar el reclamo: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 2: Listar Reclamos (solo para ADMIN)
     * Para el panel de administrador - debe ver reclamos de TODOS los estudiantes?
     * O solo los del esquema actual?
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarReclamos() {
        try {
            String schema = getCurrentSchema();
            System.out.println("📋 Listando reclamos del esquema: " + schema);

            // Para el estudiante: solo ve sus reclamos
            String sql = "SELECT * FROM " + schema + ".reclamos ORDER BY id DESC";
            List<Reclamo> lista = entityManager.createNativeQuery(sql, Reclamo.class).getResultList();

            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener los reclamos: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 3: Listar TODOS los reclamos (solo para ADMIN)
     * Para que el administrador vea reclamos de todos los estudiantes
     */
    @GetMapping("/listar-todos")
    public ResponseEntity<?> listarTodosReclamos() {
        try {
            // Esta consulta sería más compleja porque necesitarías
            // buscar en todos los esquemas estudiante_*
            // Por ahora, solo para ADMIN con esquema public
            List<Reclamo> lista = entityManager.createQuery("SELECT r FROM Reclamo r", Reclamo.class).getResultList();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener los reclamos: " + e.getMessage());
        }
    }
}