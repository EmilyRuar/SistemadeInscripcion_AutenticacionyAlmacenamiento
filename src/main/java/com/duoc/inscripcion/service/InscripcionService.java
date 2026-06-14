package com.duoc.inscripcion.service;

import com.duoc.inscripcion.dto.*;
import com.duoc.inscripcion.exception.ResourceNotFoundException;
import com.duoc.inscripcion.model.*;
import com.duoc.inscripcion.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoService cursoService;

    /**
     * Inscribe a un estudiante en uno o más cursos y devuelve el resumen completo.
     */
    @Transactional
    public InscripcionResponseDTO inscribir(InscripcionRequestDTO dto) {
        List<Curso> cursos = cursoService.buscarPorIds(dto.getCursoIds());

        BigDecimal total = cursos.stream()
            .map(Curso::getCosto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Inscripcion inscripcion = Inscripcion.builder()
            .nombreEstudiante(dto.getNombreEstudiante())
            .emailEstudiante(dto.getEmailEstudiante())
            .cursos(cursos)
            .totalPagar(total)
            .build();

        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        return toResponseDTO(guardada);
    }

    /**
     * Obtiene una inscripción por ID. Lanza ResourceNotFoundException si no existe.
     */
    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerPorId(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Inscripción no encontrada con ID: " + id));
        return toResponseDTO(inscripcion);
    }

    /**
     * Convierte entidad Inscripcion a DTO de respuesta.
     */
    private InscripcionResponseDTO toResponseDTO(Inscripcion inscripcion) {
        List<CursoResumenDTO> cursosResumen = inscripcion.getCursos().stream()
            .map(c -> CursoResumenDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .instructor(c.getInstructor())
                .costo(c.getCosto())
                .build())
            .collect(Collectors.toList());

        return InscripcionResponseDTO.builder()
            .id(inscripcion.getId())
            .nombreEstudiante(inscripcion.getNombreEstudiante())
            .emailEstudiante(inscripcion.getEmailEstudiante())
            .fechaInscripcion(inscripcion.getFechaInscripcion())
            .cursos(cursosResumen)
            .totalPagar(inscripcion.getTotalPagar())
            .build();
    }
}
