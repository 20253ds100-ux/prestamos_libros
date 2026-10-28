package com.example.libros.repository;

import com.example.libros.model.EstadoLibro;
import com.example.libros.model.Lector;
import com.example.libros.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.lector = :lector AND p.estado IN (:estados)")
    long countByLectorAndEstadoIn(
            @Param("lector") Lector lector,
            @Param("estados") List<EstadoLibro> estados
    );

    @Query("SELECT p FROM Prestamo p WHERE p.lector.id = :lectorId AND p.estado IN ('PRESTADO', 'VENCIDO')")
    List<Prestamo> findPrestamosActivosByLectorId(@Param("lectorId") Long lectorId);
}