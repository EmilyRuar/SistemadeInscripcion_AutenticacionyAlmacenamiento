package com.duoc.inscripcion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionResponseDTO {
    private Long id;
    private String nombreEstudiante;
    private String emailEstudiante;
    private LocalDateTime fechaInscripcion;
    private List<CursoResumenDTO> cursos;
    private BigDecimal totalPagar;
}
