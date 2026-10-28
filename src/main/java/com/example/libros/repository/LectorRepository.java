package com.example.libros.repository;

import com.example.libros.model.Lector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectorRepository extends JpaRepository<Lector, Long> {
}