package com.duoc.inscripcion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "CURSOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "curso_seq")
    @SequenceGenerator(name = "curso_seq", sequenceName = "CURSO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombre;

    @NotBlank(message = "El instructor es obligatorio")
    @Size(max = 150, message = "El nombre del instructor no puede superar 150 caracteres")
    @Column(name = "INSTRUCTOR", nullable = false, length = 150)
    private String instructor;

    @NotBlank(message = "La duración es obligatoria")
    @Size(max = 50, message = "La duración no puede superar 50 caracteres")
    @Column(name = "DURACION", nullable = false, length = 50)
    private String duracion;

    @NotNull(message = "El costo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a 0")
    @Column(name = "COSTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @JsonIgnore
    @ManyToMany(mappedBy = "cursos")
    private List<Inscripcion> inscripciones;
}
