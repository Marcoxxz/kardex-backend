package com.universidad.kardex_backend.controller;

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

    @Autowired // ← AGREGA ESTO
    private EstudianteRepository estudianteRepository;

    // Endpoint principal: Obtener kardex completo de un estudiante
    @GetMapping("/estudiante/{ru}")
    public ResponseEntity<Map<String, Object>> getKardexEstudiante(@PathVariable String ru) {
        List<Nota> notas = notaRepository.findByRu(ru);

        if (notas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "Estudiante sin notas registradas"));
        }

        // Calcular métricas
        int totalCreditos = 0;
        int sumaPonderada = 0;
        int materiasAprobadas = 0;
        int materiasReprobadas = 0;

        for (Nota nota : notas) {
            int creditos = nota.getCreditos() != null ? nota.getCreditos() : 0;
            int notaFinal = nota.getNotaFinal();

            totalCreditos += creditos;
            sumaPonderada += notaFinal * creditos;

            if (notaFinal >= 51) { // Asumiendo que 51 es la nota mínima aprobatoria
                materiasAprobadas++;
            } else {
                materiasReprobadas++;
            }
        }

        double promedioPonderado = totalCreditos > 0 ? (double) sumaPonderada / totalCreditos : 0;

        // Construir respuesta
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ru", ru);
        Optional<Estudiante> estudiante = estudianteRepository.findById(ru);
        String nombreCompleto = estudiante.map(e -> e.getNombres() + " " + e.getApellidos())
                .orElse("Estudiante " + ru);
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

    // Método auxiliar (mejóralo consultando tu tabla Estudiante)
    private String obtenerNombreEstudiante(String ru) {
        // Aquí deberías inyectar EstudianteRepository y buscar el nombre
        return "Estudiante " + ru;
    }
}