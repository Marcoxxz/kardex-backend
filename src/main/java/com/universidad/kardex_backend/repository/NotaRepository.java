package com.universidad.kardex_backend.repository;

import com.universidad.kardex_backend.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    
    // ============ CONSULTAS BÁSICAS ============
    
    // Buscar todas las notas de un estudiante por su RU
    List<Nota> findByRu(String ru);
    
    // Buscar notas de un estudiante en una gestión específica
    List<Nota> findByRuAndGestion(String ru, String gestion);
    
    // Buscar notas de un estudiante por sigla de materia
    List<Nota> findByRuAndSiglaMateria(String ru, String siglaMateria);
    
    // Buscar una nota específica (estudiante + materia + gestión)
    Optional<Nota> findByRuAndSiglaMateriaAndGestion(String ru, String siglaMateria, String gestion);
    
    // ============ CONSULTAS PARA KARDEX ============
    
    // Obtener todas las notas aprobadas de un estudiante (nota >= 51)
    @Query("SELECT n FROM Nota n WHERE n.ru = :ru AND n.notaFinal >= 51")
    List<Nota> findNotasAprobadasByRu(@Param("ru") String ru);
    
    // Obtener todas las notas reprobadas de un estudiante (nota < 51)
    @Query("SELECT n FROM Nota n WHERE n.ru = :ru AND n.notaFinal < 51")
    List<Nota> findNotasReprobadasByRu(@Param("ru") String ru);
    
    // Calcular suma de créditos de materias aprobadas
    @Query("SELECT SUM(n.creditos) FROM Nota n WHERE n.ru = :ru AND n.notaFinal >= 51")
    Integer sumCreditosAprobadosByRu(@Param("ru") String ru);
    
    // Calcular suma ponderada (nota * créditos) para promedio
    @Query("SELECT SUM(n.notaFinal * n.creditos) FROM Nota n WHERE n.ru = :ru")
    Integer sumPonderadaByRu(@Param("ru") String ru);
    
    // Calcular total de créditos cursados
    @Query("SELECT SUM(n.creditos) FROM Nota n WHERE n.ru = :ru")
    Integer sumTotalCreditosByRu(@Param("ru") String ru);
    
    // ============ CONSULTAS DE VALIDACIÓN ============
    
    // Verificar si un estudiante ya cursó una materia
    boolean existsByRuAndSiglaMateria(String ru, String siglaMateria);
    
    // Verificar si un estudiante aprobó una materia específica
    @Query("SELECT COUNT(n) > 0 FROM Nota n WHERE n.ru = :ru AND n.siglaMateria = :sigla AND n.notaFinal >= 51")
    boolean existsMateriaAprobada(@Param("ru") String ru, @Param("sigla") String sigla);
    
    // Obtener la mejor nota de un estudiante en una materia (si la repitió)
    @Query("SELECT MAX(n.notaFinal) FROM Nota n WHERE n.ru = :ru AND n.siglaMateria = :sigla")
    Integer getMejorNotaByRuAndSigla(@Param("ru") String ru, @Param("sigla") String sigla);
    
    // ============ CONSULTAS ESTADÍSTICAS ============
    
    // Contar materias aprobadas por estudiante
    @Query("SELECT COUNT(n) FROM Nota n WHERE n.ru = :ru AND n.notaFinal >= 51")
    Long countMateriasAprobadasByRu(@Param("ru") String ru);
    
    // Contar materias reprobadas por estudiante
    @Query("SELECT COUNT(n) FROM Nota n WHERE n.ru = :ru AND n.notaFinal < 51")
    Long countMateriasReprobadasByRu(@Param("ru") String ru);
    
    // Obtener promedio simple (sin ponderar) por estudiante
    @Query("SELECT AVG(n.notaFinal) FROM Nota n WHERE n.ru = :ru")
    Double getPromedioSimpleByRu(@Param("ru") String ru);
    
    // Obtener todas las gestiones donde un estudiante tiene notas
    @Query("SELECT DISTINCT n.gestion FROM Nota n WHERE n.ru = :ru ORDER BY n.gestion DESC")
    List<String> findGestionesByRu(@Param("ru") String ru);
    
    // ============ CONSULTAS POR GESTIÓN ============
    
    // Obtener kardex agrupado por gestión
    @Query("SELECT n FROM Nota n WHERE n.ru = :ru ORDER BY n.gestion DESC, n.siglaMateria ASC")
    List<Nota> findKardexCompleto(@Param("ru") String ru);
    
    // Obtener resumen por gestión (promedio y créditos por gestión)
    @Query("SELECT n.gestion, SUM(n.creditos) as totalCreditos, AVG(n.notaFinal) as promedio " +
           "FROM Nota n WHERE n.ru = :ru GROUP BY n.gestion ORDER BY n.gestion DESC")
    List<Object[]> findResumenPorGestion(@Param("ru") String ru);
    
    // ============ CONSULTAS PARA REPORTES ============
    
    // Obtener todas las notas de una materia específica (para estadísticas)
    List<Nota> findBySiglaMateria(String siglaMateria);
    
    // Obtener promedio general de una materia
    @Query("SELECT AVG(n.notaFinal) FROM Nota n WHERE n.siglaMateria = :sigla")
    Double getPromedioMateria(@Param("sigla") String sigla);
    
    // Obtener tasa de aprobación de una materia
    @Query("SELECT COUNT(CASE WHEN n.notaFinal >= 51 THEN 1 END) * 100.0 / COUNT(n) " +
           "FROM Nota n WHERE n.siglaMateria = :sigla")
    Double getTasaAprobacionMateria(@Param("sigla") String sigla);
}