package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Estudiante;
import com.universidad.kardex_backend.model.Nota;
import com.universidad.kardex_backend.repository.EstudianteRepository;
import com.universidad.kardex_backend.repository.NotaRepository;

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

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    /**
     * Obtener kardex completo de un estudiante
     */
    @GetMapping("/estudiante/{ru}")
    public ResponseEntity<Map<String, Object>> getKardexEstudiante(
            @PathVariable String ru) {

        String schema = getCurrentSchema();

        List<Nota> notas = notaRepository.findByRu(ru);

        if (notas.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Estudiante sin notas registradas",
                    "schema", schema));
        }

        int totalCreditos = 0;
        int sumaPonderada = 0;
        int materiasAprobadas = 0;
        int materiasReprobadas = 0;

        for (Nota nota : notas) {

            int creditos = nota.getCreditos() != null
                    ? nota.getCreditos()
                    : 0;

            int notaFinal = nota.getNotaFinal() != null
                    ? nota.getNotaFinal()
                    : 0;

            totalCreditos += creditos;
            sumaPonderada += notaFinal * creditos;

            if (notaFinal >= 51) {
                materiasAprobadas++;
            } else {
                materiasReprobadas++;
            }
        }

        double promedioPonderado = totalCreditos > 0
                ? (double) sumaPonderada / totalCreditos
                : 0;

        Optional<Estudiante> estudiante = estudianteRepository.findById(ru);

        String nombreCompleto = estudiante
                .map(e -> e.getNombres() + " " + e.getApellidos())
                .orElse("Estudiante " + ru);

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
    }
}