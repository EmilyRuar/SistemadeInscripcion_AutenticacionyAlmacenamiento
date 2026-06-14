package com.duoc.inscripcion.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResponseDTO {
    private Long id;
    private String nombre;
    private String instructor;
    private String duracion;
    private BigDecimal costo;
}
