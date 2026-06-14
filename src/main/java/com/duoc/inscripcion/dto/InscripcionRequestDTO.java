package com.duoc.inscripcion.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionRequestDTO {

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombreEstudiante;

    @NotBlank(message = "El email del estudiante es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    private String emailEstudiante;

    @NotEmpty(message = "Debe seleccionar al menos un curso")
    private List<Long> cursoIds;
}
