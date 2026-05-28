package com.universidad.kardex_backend.controller;

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

    // ENDPOINT VULNERABLE: Permite inyección SQL basada en UNION o Ciega
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorCi(@RequestParam("ci") String ci) {
        try {
            // Mecanismo inseguro: Concatenación directa de la entrada del usuario
            String sql = "SELECT ru, apellidos, carrera, ci, nombres FROM estudiantes WHERE ci = '" + ci + "'";

            Query query = entityManager.createNativeQuery(sql, Estudiante.class);
            List<Estudiante> resultados = query.getResultList();

            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            // Retornar el mensaje de error de SQL es útil en laboratorios iniciales
            // (Error-Based SQLi)
            return ResponseEntity.status(500).body("Error en la consulta: " + e.getMessage());
        }
    }
}
