package com.example.libros.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Table(name = "Prestamo")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Lector lector;

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;
    private LocalDate fechaPrestamo;
    @Column(nullable = false)
    private LocalDate fechaDevolucionMaxima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLibro estado;
}
