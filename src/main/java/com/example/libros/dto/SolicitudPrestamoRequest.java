package com.example.libros.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitudPrestamoRequest {

    @NotBlank(message = "el id del lector no debe ir vacip")
    private Long lectorId;

    @NotBlank(message = "No puede ir vacio el id del libro")
    private Long libroId;
}
