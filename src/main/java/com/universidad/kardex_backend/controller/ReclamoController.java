package com.universidad.kardex_backend.controller;

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

    /**
     * ENDPOINT 1: Enviar Reclamo (Almacenamiento del Payload)
     * El estudiante envía su queja. El sistema la guarda textualmente sin verificar 
     * si contiene código JavaScript malicioso.
     */
    @PostMapping("/enviar")
    @Transactional
    public ResponseEntity<?> enviarReclamo(@RequestBody Reclamo reclamo) {
        try {
            entityManager.persist(reclamo);
            return ResponseEntity.ok("Reclamo o sugerencia enviada exitosamente al buzón de la Carrera.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar el reclamo: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 2: Panel del Administrador / Director (Disparador del XSS)
     * Devuelve todos los reclamos en texto plano para que el "admin" los lea. 
     * Al no sanitizar la salida, si hay un script guardado, se enviará al cliente tal cual.
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarReclamos() {
        try {
            List<Reclamo> lista = entityManager.createQuery("SELECT r FROM Reclamo r", Reclamo.class).getResultList();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener los reclamos: " + e.getMessage());
        }
    }
}
