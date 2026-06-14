package com.duoc.inscripcion.controller;

import com.duoc.inscripcion.dto.*;
import com.duoc.inscripcion.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    /**
     * GET /api/cursos
     * Lista todos los cursos disponibles con nombre, instructor, duración y costo.
     */
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarCursos() {
        return ResponseEntity.ok(cursoService.listarCursos());
    }

    /**
     * POST /api/cursos
     * Agrega un nuevo curso a la oferta educativa.
     */
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crearCurso(@Valid @RequestBody CursoRequestDTO dto) {
        CursoResponseDTO creado = cursoService.crearCurso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
