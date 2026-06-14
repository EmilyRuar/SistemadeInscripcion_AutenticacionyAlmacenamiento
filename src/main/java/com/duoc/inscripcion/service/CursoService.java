package com.duoc.inscripcion.service;

import com.duoc.inscripcion.dto.*;
import com.duoc.inscripcion.exception.ResourceNotFoundException;
import com.duoc.inscripcion.model.Curso;
import com.duoc.inscripcion.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    /**
     * Retorna todos los cursos disponibles.
     */
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursos() {
        return cursoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo curso en la base de datos.
     */
    @Transactional
    public CursoResponseDTO crearCurso(CursoRequestDTO dto) {
        Curso curso = Curso.builder()
                .nombre(dto.getNombre())
                .instructor(dto.getInstructor())
                .duracion(dto.getDuracion())
                .costo(dto.getCosto())
                .build();
        Curso guardado = cursoRepository.save(curso);
        return toResponseDTO(guardado);
    }

    /**
     * Busca cursos por sus IDs. Lanza excepción si alguno no existe.
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarPorIds(List<Long> ids) {
        return ids.stream().map(id ->
                cursoRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id))
        ).collect(Collectors.toList());
    }

    /**
     * Convierte entidad Curso a DTO de respuesta.
     */
    public CursoResponseDTO toResponseDTO(Curso curso) {
        return CursoResponseDTO.builder()
                .id(curso.getId())
                .nombre(curso.getNombre())
                .instructor(curso.getInstructor())
                .duracion(curso.getDuracion())
                .costo(curso.getCosto())
                .build();
    }
}
