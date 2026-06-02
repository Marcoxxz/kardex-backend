package com.universidad.kardex_backend.controller;

import com.universidad.kardex_backend.config.SchemaInterceptor;
import com.universidad.kardex_backend.model.Materia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/materias")
@CrossOrigin(origins = "*")
public class MateriaController {

    @PersistenceContext
    private EntityManager entityManager;

    private String getCurrentSchema() {
        String schema = SchemaInterceptor.getCurrentSchema();
        return schema != null ? schema : "public";
    }

    // Obtener todas las materias (desde el esquema del estudiante)
    @GetMapping
    public ResponseEntity<List<Materia>> getAllMaterias() {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias";
            List<Materia> materias = entityManager.createNativeQuery(sql, Materia.class).getResultList();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener materias activas
    @GetMapping("/activas")
    public ResponseEntity<List<Materia>> getMateriasActivas() {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias WHERE activo = true";
            List<Materia> materias = entityManager.createNativeQuery(sql, Materia.class).getResultList();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener materia por sigla
    @GetMapping("/{sigla}")
    public ResponseEntity<Materia> getMateriaBySigla(@PathVariable String sigla) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias WHERE sigla = '" + sigla + "'";
            Materia materia = (Materia) entityManager.createNativeQuery(sql, Materia.class).getSingleResult();
            return new ResponseEntity<>(materia, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Buscar materias por carrera
    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<List<Materia>> getMateriasByCarrera(@PathVariable String carrera) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias WHERE carrera = '" + carrera + "'";
            List<Materia> materias = entityManager.createNativeQuery(sql, Materia.class).getResultList();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar materias por semestre
    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<List<Materia>> getMateriasBySemestre(@PathVariable Integer semestre) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias WHERE semestre = " + semestre;
            List<Materia> materias = entityManager.createNativeQuery(sql, Materia.class).getResultList();
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
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias WHERE carrera = '" + carrera + "' AND semestre = "
                    + semestre;
            List<Materia> materias = entityManager.createNativeQuery(sql, Materia.class).getResultList();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar materias por término de búsqueda
    @GetMapping("/buscar")
    public ResponseEntity<List<Materia>> searchMaterias(@RequestParam String term) {
        try {
            String schema = getCurrentSchema();
            String sql = "SELECT * FROM " + schema + ".materias WHERE LOWER(nombre) LIKE LOWER('%" + term
                    + "%') OR LOWER(sigla) LIKE LOWER('%" + term + "%')";
            List<Materia> materias = entityManager.createNativeQuery(sql, Materia.class).getResultList();
            return new ResponseEntity<>(materias, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Crear nueva materia (solo para ADMIN - en esquema public)
    // ⚠️ Los estudiantes NO deberían poder crear materias
    @PostMapping
    public ResponseEntity<?> createMateria(@RequestBody Materia materia) {
        try {
            String schema = getCurrentSchema();

            String verificarSql = "SELECT COUNT(*) FROM " + schema + ".materias " +
                    "WHERE sigla = '" + materia.getSigla() + "'";

            Number existe = (Number) entityManager
                    .createNativeQuery(verificarSql)
                    .getSingleResult();

            if (existe.intValue() > 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("La materia ya existe");
            }

            String sql = "INSERT INTO " + schema + ".materias " +
                    "(sigla, nombre, creditos, carrera, semestre, requisito, area, activo) " +
                    "VALUES ('" +
                    materia.getSigla() + "', '" +
                    materia.getNombre() + "', " +
                    materia.getCreditos() + ", '" +
                    materia.getCarrera() + "', " +
                    materia.getSemestre() + ", " +
                    (materia.getRequisito() == null
                            ? "NULL"
                            : "'" + materia.getRequisito() + "'")
                    +
                    ", '" + materia.getArea() + "', " +
                    materia.getActivo() + ")";

            entityManager.createNativeQuery(sql).executeUpdate();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Materia creada correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // Actualizar materia existente (solo ADMIN)
    @PutMapping("/{sigla}")
    @Transactional
    public ResponseEntity<?> updateMateria(
            @PathVariable String sigla,
            @RequestBody Materia materia) {

        try {
            String schema = getCurrentSchema();

            String sql = "UPDATE " + schema + ".materias SET " +
                    "nombre = '" + materia.getNombre() + "', " +
                    "creditos = " + materia.getCreditos() + ", " +
                    "carrera = '" + materia.getCarrera() + "', " +
                    "semestre = " + materia.getSemestre() + ", " +
                    "requisito = " +
                    (materia.getRequisito() == null
                            ? "NULL"
                            : "'" + materia.getRequisito() + "'")
                    +
                    ", area = '" + materia.getArea() + "', " +
                    "activo = " + materia.getActivo() +
                    " WHERE sigla = '" + sigla + "'";

            int filas = entityManager
                    .createNativeQuery(sql)
                    .executeUpdate();

            if (filas == 0) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok("Materia actualizada");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // Eliminar materia (solo ADMIN)
    @DeleteMapping("/{sigla}")
    @Transactional
    public ResponseEntity<?> deleteMateria(@PathVariable String sigla) {

        try {
            String schema = getCurrentSchema();

            String sql = "DELETE FROM " + schema + ".materias " +
                    "WHERE sigla = '" + sigla + "'";

            System.out.println("SCHEMA DELETE = " + schema);
            System.out.println("SQL = " + sql);

            int filas = entityManager
                    .createNativeQuery(sql)
                    .executeUpdate();

            if (filas == 0) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // Soft delete (desactivar materia) - solo ADMIN
    @PatchMapping("/{sigla}/desactivar")
    @Transactional
    public ResponseEntity<?> desactivarMateria(@PathVariable String sigla) {

        try {
            String schema = getCurrentSchema();

            String sql = "UPDATE " + schema + ".materias " +
                    "SET activo = false " +
                    "WHERE sigla = '" + sigla + "'";

            System.out.println("SCHEMA PATCH = " + schema);
            System.out.println("SQL = " + sql);

            int filas = entityManager
                    .createNativeQuery(sql)
                    .executeUpdate();

            if (filas == 0) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok("Materia desactivada");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // Activar materia - solo ADMIN
    @PatchMapping("/{sigla}/activar")
    @Transactional
    public ResponseEntity<?> activarMateria(@PathVariable String sigla) {

        try {
            String schema = getCurrentSchema();

            String sql = "UPDATE " + schema + ".materias " +
                    "SET activo = true " +
                    "WHERE sigla = '" + sigla + "'";

            int filas = entityManager
                    .createNativeQuery(sql)
                    .executeUpdate();

            if (filas == 0) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok("Materia activada");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}