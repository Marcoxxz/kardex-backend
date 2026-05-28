package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // Permite que Angular se conecte sin problemas de CORS
public class AuthController {

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1. Convertir la contraseña que ingresa el usuario a MD5 para compararla
            String passwordMd5 = convertirAMd5(request.getPassword());

            // 2. VULNERABILIDAD ADICIONAL: Consulta SQL armada por concatenación directa
            // strings.
            // Esto permite saltarse el login usando inyecciones como: admin' --
            String sql = "SELECT * FROM usuarios WHERE username = '" + request.getUsername() + "' AND password = '"
                    + passwordMd5 + "'";

            Query query = entityManager.createNativeQuery(sql, Usuario.class);
            List<Usuario> resultados = query.getResultList();

            // 3. Evaluar si se encontró al usuario
            if (!resultados.isEmpty()) {
                Usuario usuarioLogueado = resultados.get(0);
                // Simulamos una respuesta exitosa devolviendo sus datos y un mensaje
                // descriptivo
                return ResponseEntity.ok(usuarioLogueado);
            } else {
                return ResponseEntity.status(401).body("Credenciales incorrectas o usuario no encontrado.");
            }

        } catch (Exception e) {
            // Retorna el error detallado del motor PostgreSQL, ideal para "Error-Based
            // SQLi" en el login
            return ResponseEntity.status(500).body("Error interno en la consulta SQL: " + e.getMessage());
        }
    }

    // ====================================================================
    // VULNERABILIDAD 3: ENDPOINT EXPUESTO / FUGA DE INFORMACIÓN
    // Ruta oculta de desarrollo que un estudiante descubrirá usando herramientas de fuzzing
    // ====================================================================
    @GetMapping("/debug-list-users-dev-all")
    public ResponseEntity<?> exportAllUsersDev() {
        try {
            String sql = "SELECT id, nombre_real, password, rol, username FROM usuarios";
            Query query = entityManager.createNativeQuery(sql);
            return ResponseEntity.ok(query.getResultList());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Método utilitario para convertir texto plano a Hash MD5
    private String convertirAMd5(String texto) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(texto.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
