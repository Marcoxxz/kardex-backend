package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.model.Materia;
import com.universidad.kardex_backend.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/materias")
@CrossOrigin(origins = "*") // Ajusta según tu frontend
public class MateriaController {
    
    @Autowired
    private MateriaRepository materiaRepository;
    
    // Obtener todas las materias
    @GetMapping
    public ResponseEntity<List<Materia>> getAllMaterias() {
        try {
            List<Materia> materias = materiaRepository.findAll();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Obtener materias activas
    @GetMapping("/activas")
    public ResponseEntity<List<Materia>> getMateriasActivas() {
        try {
            List<Materia> materias = materiaRepository.findByActivoTrue();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Obtener materia por sigla
    @GetMapping("/{sigla}")
    public ResponseEntity<Materia> getMateriaBySigla(@PathVariable String sigla) {
        Optional<Materia> materia = materiaRepository.findById(sigla);
        if (materia.isPresent()) {
            return new ResponseEntity<>(materia.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Buscar materias por carrera
    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<List<Materia>> getMateriasByCarrera(@PathVariable String carrera) {
        try {
            List<Materia> materias = materiaRepository.findByCarrera(carrera);
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Buscar materias por semestre
    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<List<Materia>> getMateriasBySemestre(@PathVariable Integer semestre) {
        try {
            List<Materia> materias = materiaRepository.findBySemestre(semestre);
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Buscar materias por carrera y semestre
    @GetMapping("/carrera/{carrera}/semestre/{semestre}")
    public ResponseEntity<List<Materia>> getMateriasByCarreraAndSemestre(
            @PathVariable String carrera, 
            @PathVariable Integer semestre) {
        try {
            List<Materia> materias = materiaRepository.findByCarreraAndSemestre(carrera, semestre);
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Buscar materias por término de búsqueda
    @GetMapping("/buscar")
    public ResponseEntity<List<Materia>> searchMaterias(@RequestParam String term) {
        try {
            List<Materia> materias = materiaRepository.searchByNombreOrSigla(term);
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Crear nueva materia (solo para ADMIN)
    @PostMapping
    public ResponseEntity<Materia> createMateria(@RequestBody Materia materia) {
        try {
            // Verificar si ya existe
            if (materiaRepository.existsById(materia.getSigla())) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
            
            // Validar créditos positivos
            if (materia.getCreditos() <= 0) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            Materia nuevaMateria = materiaRepository.save(materia);
            return new ResponseEntity<>(nuevaMateria, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Actualizar materia existente
    @PutMapping("/{sigla}")
    public ResponseEntity<Materia> updateMateria(@PathVariable String sigla, @RequestBody Materia materia) {
        Optional<Materia> materiaData = materiaRepository.findById(sigla);
        
        if (materiaData.isPresent()) {
            Materia materiaExistente = materiaData.get();
            materiaExistente.setNombre(materia.getNombre());
            materiaExistente.setCreditos(materia.getCreditos());
            materiaExistente.setCarrera(materia.getCarrera());
            materiaExistente.setSemestre(materia.getSemestre());
            materiaExistente.setRequisito(materia.getRequisito());
            materiaExistente.setArea(materia.getArea());
            materiaExistente.setActivo(materia.getActivo());
            
            Materia materiaActualizada = materiaRepository.save(materiaExistente);
            return new ResponseEntity<>(materiaActualizada, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Eliminar materia (soft delete o hard delete)
    @DeleteMapping("/{sigla}")
    public ResponseEntity<Map<String, Boolean>> deleteMateria(@PathVariable String sigla) {
        try {
            Optional<Materia> materia = materiaRepository.findById(sigla);
            if (materia.isPresent()) {
                materiaRepository.deleteById(sigla);
                Map<String, Boolean> response = new HashMap<>();
                response.put("deleted", Boolean.TRUE);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Soft delete (desactivar materia)
    @PatchMapping("/{sigla}/desactivar")
    public ResponseEntity<Materia> desactivarMateria(@PathVariable String sigla) {
        Optional<Materia> materiaData = materiaRepository.findById(sigla);
        
        if (materiaData.isPresent()) {
            Materia materia = materiaData.get();
            materia.setActivo(false);
            Materia materiaActualizada = materiaRepository.save(materia);
            return new ResponseEntity<>(materiaActualizada, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Activar materia
    @PatchMapping("/{sigla}/activar")
    public ResponseEntity<Materia> activarMateria(@PathVariable String sigla) {
        Optional<Materia> materiaData = materiaRepository.findById(sigla);
        
        if (materiaData.isPresent()) {
            Materia materia = materiaData.get();
            materia.setActivo(true);
            Materia materiaActualizada = materiaRepository.save(materia);
            return new ResponseEntity<>(materiaActualizada, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}