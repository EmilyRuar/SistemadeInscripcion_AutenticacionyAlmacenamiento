package com.duoc.inscripcion.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombre;

    @NotBlank(message = "El instructor es obligatorio")
    @Size(max = 150, message = "El nombre del instructor no puede superar 150 caracteres")
    private String instructor;

    @NotBlank(message = "La duración es obligatoria")
    @Size(max = 50, message = "La duración no puede superar 50 caracteres")
    private String duracion;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a 0")
    private BigDecimal costo;
}
