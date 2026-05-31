package com.universidad.kardex_backend.repository;

import com.universidad.kardex_backend.model.Estudiante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {
    // El ID es el RU (String)
    List<Estudiante> findByCi(String ci);
    List<Estudiante> findByCarrera(String carrera);
}