package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.model.Nota;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notas")
@CrossOrigin(origins = "*")
public class NotaController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * VULNERABILIDAD IDOR (Insecure Direct Object Reference)
     * El endpoint expone la llave primaria directamente en la URL. 
     * Cualquier estudiante autenticado puede cambiar el {id} correlativo 
     * y acceder a las notas privadas de cualquier otro estudiante.
     */
    @GetMapping("/consultar/{id}")
    public ResponseEntity<?> consultarNotaPorId(@PathVariable("id") Long id) {
        try {
            // Buscamos la nota directamente por su ID de registro
            Nota nota = entityManager.find(Nota.class, id);
            
            if (nota != null) {
                // FALLO: Se retorna el objeto directamente sin verificar el contexto del usuario actual
                return ResponseEntity.ok(nota);
            } else {
                return ResponseEntity.status(404).body("Registro de calificación no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
}
