package com.example.libros.dto;

import com.example.libros.model.EstadoLibro;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PrestamoResponse {
    private Long id;
    private Long lectorId;
    private String nombreLector;
    private Long libroId;
    private String tituloLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionMaxima;
    private EstadoLibro estado;
}
