package com.example.libros.service;

import com.example.libros.dto.PrestamoResponse;
import com.example.libros.dto.SolicitudPrestamoRequest;
import com.example.libros.exceptions.ErrorHandler;
import com.example.libros.exceptions.BusinessRuleException;
import com.example.libros.exceptions.CustomBadRequestException;
import com.example.libros.model.*;
import com.example.libros.repository.LectorRepository;
import com.example.libros.repository.LibroRepository;
import com.example.libros.repository.*;
import com.example.libros.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LectorRepository lectorRepository;
    private final LibroRepository libroRepository;

    @Transactional
    public PrestamoResponse solicitarPrestamo(SolicitudPrestamoRequest request) {
        Lector lector = lectorRepository.findById(request.getLectorId())
                .orElseThrow(() -> new CustomBadRequestException("El lector no existe"));

        if (!lector.isActivo()) {
            throw new BusinessRuleException("El lector está inactivo/bloqueado");
        }
        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new CustomBadRequestException("El libro no existe"));
        if (libro.getEjemplaresDisponibles() < 1) {
            throw new BusinessRuleException("El libro no tiene ejemplares disponibles");
        }
        int limiteMaximo = lector.isMembresiaPremium() ? 5 : 3;
        long prestamosActivos = prestamoRepository.countByLectorAndEstadoIn(
                lector, List.of(EstadoLibro.PRESTADO, EstadoLibro.VENCIDO)
        );

        if (prestamosActivos >= limiteMaximo) {
            throw new BusinessRuleException(
                    "El lector ha alcanzado el límite máximo de préstamos activos (" + limiteMaximo + ")"
            );
        }

        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() - 1);
        libroRepository.save(libro);
        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = Prestamo.builder()
                .lector(lector)
                .libro(libro)
                .fechaPrestamo(hoy)
                .fechaDevolucionMaxima(hoy.plusDays(14))
                .estado(EstadoLibro.PRESTADO)
                .build();

        return mapToResponse(prestamoRepository.save(prestamo));
    }
    @Transactional
    public PrestamoResponse devolverLibro(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new CustomBadRequestException("El préstamo no existe"));

        if (prestamo.getEstado() == EstadoLibro.DEVUELTO) {
            throw new BusinessRuleException("El préstamo ya fue devuelto anteriormente");
        }

        LocalDate hoy = LocalDate.now();
        Lector lector = prestamo.getLector();

        if (hoy.isAfter(prestamo.getFechaDevolucionMaxima())) {
            lector.setActivo(false);
            lectorRepository.save(lector);
        }

        prestamo.setEstado(EstadoLibro.DEVUELTO);

        Libro libro = prestamo.getLibro();
        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() + 1);
        libroRepository.save(libro);

        return mapToResponse(prestamoRepository.save(prestamo));
    }
    @Transactional
    public PrestamoResponse renovarPrestamo(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new CustomBadRequestException("El prestamo no existe"));

        if (prestamo.getEstado() == EstadoLibro.VENCIDO || prestamo.getEstado() == EstadoLibro.DEVUELTO) {
            throw new BusinessRuleException("no se puede renovar un preestamo en estado " + prestamo.getEstado());
        }

        prestamo.setFechaDevolucionMaxima(prestamo.getFechaDevolucionMaxima().plusDays(7));
        return mapToResponse(prestamoRepository.save(prestamo));
    }

    @Transactional
    public void rehabilitarLector(Long lectorId) {
        Lector lector = lectorRepository.findById(lectorId)
                .orElseThrow(() -> new CustomBadRequestException("El lector no existe"));

        lector.setActivo(true);
        lectorRepository.save(lector);
    }
    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPrestamosActivosLector(Long lectorId) {
        if (!lectorRepository.existsById(lectorId)) {
            throw new CustomBadRequestException("El lector no existe");
        }
        return prestamoRepository.findPrestamosActivosByLectorId(lectorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PrestamoResponse mapToResponse(Prestamo p) {
        return PrestamoResponse.builder()
                .id(p.getId())
                .lectorId(p.getLector().getId())
                .nombreLector(p.getLector().getNombre())
                .libroId(p.getLibro().getId())
                .tituloLibro(p.getLibro().getTitulo())
                .fechaPrestamo(p.getFechaPrestamo())
                .fechaDevolucionMaxima(p.getFechaDevolucionMaxima())
                .estado(p.getEstado())
                .build();
    }
}