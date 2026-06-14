package com.duoc.inscripcion.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResumenDTO {
    private Long id;
    private String nombre;
    private String instructor;
    private BigDecimal costo;
}
