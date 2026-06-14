package com.duoc.inscripcion.controller;

import com.duoc.inscripcion.dto.*;
import com.duoc.inscripcion.service.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Gestión de inscripciones a cursos")
@SecurityRequirement(name = "bearerAuth")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @Operation(
        summary = "Crear inscripción",
        description = "Inscribe a un estudiante en uno o más cursos. Retorna el resumen con detalle y total a pagar."
    )
    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> inscribir(
            @Valid @RequestBody InscripcionRequestDTO dto) {
        InscripcionResponseDTO respuesta = inscripcionService.inscribir(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Obtener inscripción por ID")
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.obtenerPorId(id));
    }
}
