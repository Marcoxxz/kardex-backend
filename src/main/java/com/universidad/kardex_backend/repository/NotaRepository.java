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

       List<Nota> findByRu(String ru);

       List<Nota> findByRuAndGestion(String ru, String gestion);

       List<Nota> findByRuAndSiglaMateria(String ru, String siglaMateria);

       Optional<Nota> findByRuAndSiglaMateriaAndGestion(String ru, String siglaMateria, String gestion);

       // ============ CONSULTAS PARA KARDEX ============

       @Query("SELECT n FROM Nota n WHERE n.ru = :ru AND n.notaFinal >= 51")
       List<Nota> findNotasAprobadasByRu(@Param("ru") String ru);

       @Query("SELECT n FROM Nota n WHERE n.ru = :ru AND n.notaFinal < 51")
       List<Nota> findNotasReprobadasByRu(@Param("ru") String ru);

       @Query("SELECT SUM(n.creditos) FROM Nota n WHERE n.ru = :ru AND n.notaFinal >= 51")
       Integer sumCreditosAprobadosByRu(@Param("ru") String ru);

       @Query("SELECT SUM(n.notaFinal * n.creditos) FROM Nota n WHERE n.ru = :ru")
       Integer sumPonderadaByRu(@Param("ru") String ru);

       @Query("SELECT SUM(n.creditos) FROM Nota n WHERE n.ru = :ru")
       Integer sumTotalCreditosByRu(@Param("ru") String ru);

       // ============ CONSULTAS DE VALIDACIÓN ============

       boolean existsByRuAndSiglaMateria(String ru, String siglaMateria);

       @Query("SELECT COUNT(n) > 0 FROM Nota n WHERE n.ru = :ru AND n.siglaMateria = :sigla AND n.notaFinal >= 51")
       boolean existsMateriaAprobada(@Param("ru") String ru, @Param("sigla") String sigla);

       @Query("SELECT MAX(n.notaFinal) FROM Nota n WHERE n.ru = :ru AND n.siglaMateria = :sigla")
       Integer getMejorNotaByRuAndSigla(@Param("ru") String ru, @Param("sigla") String sigla);

       // ============ CONSULTAS ESTADÍSTICAS ============

       @Query("SELECT COUNT(n) FROM Nota n WHERE n.ru = :ru AND n.notaFinal >= 51")
       Long countMateriasAprobadasByRu(@Param("ru") String ru);

       @Query("SELECT COUNT(n) FROM Nota n WHERE n.ru = :ru AND n.notaFinal < 51")
       Long countMateriasReprobadasByRu(@Param("ru") String ru);

       @Query("SELECT AVG(n.notaFinal) FROM Nota n WHERE n.ru = :ru")
       Double getPromedioSimpleByRu(@Param("ru") String ru);

       @Query("SELECT DISTINCT n.gestion FROM Nota n WHERE n.ru = :ru ORDER BY n.gestion DESC")
       List<String> findGestionesByRu(@Param("ru") String ru);

       // ============ CONSULTAS POR GESTIÓN ============

       @Query("SELECT n FROM Nota n WHERE n.ru = :ru ORDER BY n.gestion DESC, n.siglaMateria ASC")
       List<Nota> findKardexCompleto(@Param("ru") String ru);

       @Query("SELECT n.gestion, SUM(n.creditos) as totalCreditos, AVG(n.notaFinal) as promedio " +
                     "FROM Nota n WHERE n.ru = :ru GROUP BY n.gestion ORDER BY n.gestion DESC")
       List<Object[]> findResumenPorGestion(@Param("ru") String ru);

       // ============ CONSULTAS PARA REPORTES ============

       List<Nota> findBySiglaMateria(String siglaMateria);

       @Query("SELECT AVG(n.notaFinal) FROM Nota n WHERE n.siglaMateria = :sigla")
       Double getPromedioMateria(@Param("sigla") String sigla);

       @Query("SELECT COUNT(CASE WHEN n.notaFinal >= 51 THEN 1 END) * 100.0 / COUNT(n) " +
                     "FROM Nota n WHERE n.siglaMateria = :sigla")
       Double getTasaAprobacionMateria(@Param("sigla") String sigla);
}