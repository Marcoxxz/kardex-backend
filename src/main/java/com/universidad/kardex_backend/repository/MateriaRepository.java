package com.universidad.kardex_backend.repository;

import com.universidad.kardex_backend.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    // Buscar por carrera
    List<Materia> findByCarrera(String carrera);

    // Buscar por semestre
    List<Materia> findBySemestre(Integer semestre);

    // Buscar por carrera y semestre
    List<Materia> findByCarreraAndSemestre(String carrera, Integer semestre);

    // Buscar materias activas
    List<Materia> findByActivoTrue();

    // Buscar por área
    List<Materia> findByArea(String area);

    // Buscar materias sin requisito (primer semestre)
    List<Materia> findByRequisitoIsNullOrRequisito(String requisito);

    // Query personalizada para buscar por nombre o sigla
    @Query("SELECT m FROM Materia m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.sigla) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Materia> searchByNombreOrSigla(@Param("searchTerm") String searchTerm);

    // Verificar si existe una materia con requisito específico
    boolean existsBySigla(String sigla);

    // Obtener materias que tienen como requisito una materia específica
    List<Materia> findByRequisito(String requisito);
}