package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Estudiante;
import com.universidad.kardex_backend.model.Nota;
import com.universidad.kardex_backend.repository.EstudianteRepository;
import com.universidad.kardex_backend.repository.NotaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/kardex")
@CrossOrigin(origins = "*")
public class KardexController {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    @GetMapping("/estudiante/{ru}")
    public ResponseEntity<Map<String, Object>> getKardexEstudiante(@PathVariable String ru) {
        try {
            String schema = getCurrentSchema();
            System.out.println("🔍 KARDEX - Usando esquema: " + schema + " para RU: " + ru);

            // ✅ USAR nativeQuery con el esquema correcto
            String sql = "SELECT * FROM " + schema + ".notas WHERE ru = '" + ru + "'";
            List<Nota> notas = entityManager.createNativeQuery(sql, Nota.class).getResultList();

            if (notas.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Estudiante sin notas registradas",
                        "schema", schema,
                        "ru", ru));
            }

            int totalCreditos = 0;
            int sumaPonderada = 0;
            int materiasAprobadas = 0;
            int materiasReprobadas = 0;

            for (Nota nota : notas) {
                int creditos = nota.getCreditos() != null ? nota.getCreditos() : 0;
                int notaFinal = nota.getNotaFinal() != null ? nota.getNotaFinal() : 0;

                totalCreditos += creditos;
                sumaPonderada += notaFinal * creditos;

                if (notaFinal >= 51) {
                    materiasAprobadas++;
                } else {
                    materiasReprobadas++;
                }
            }

            double promedioPonderado = totalCreditos > 0 ? (double) sumaPonderada / totalCreditos : 0;

            // Obtener nombre del estudiante desde el esquema correcto
            String estudianteSql = "SELECT nombres, apellidos FROM " + schema + ".estudiantes WHERE ru = '" + ru + "'";
            String nombreCompleto = "Estudiante " + ru;
            try {
                Object[] estudianteData = (Object[]) entityManager.createNativeQuery(estudianteSql).getSingleResult();
                nombreCompleto = estudianteData[0] + " " + estudianteData[1];
            } catch (Exception e) {
                System.out.println("No se encontró nombre para RU: " + ru);
            }

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("schema", schema);
            respuesta.put("ru", ru);
            respuesta.put("nombreEstudiante", nombreCompleto);
            respuesta.put("materias", notas);
            respuesta.put("resumen", Map.of(
                    "totalCreditos", totalCreditos,
                    "promedioPonderado", Math.round(promedioPonderado * 100.0) / 100.0,
                    "materiasAprobadas", materiasAprobadas,
                    "materiasReprobadas", materiasReprobadas,
                    "totalMaterias", notas.size()));

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener kardex: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}