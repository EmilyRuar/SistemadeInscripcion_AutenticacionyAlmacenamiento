package com.duoc.inscripcion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "INSCRIPCIONES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inscripcion_seq")
    @SequenceGenerator(name = "inscripcion_seq", sequenceName = "INSCRIPCION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Column(name = "NOMBRE_ESTUDIANTE", nullable = false, length = 150)
    private String nombreEstudiante;

    @NotBlank(message = "El email del estudiante es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    @Column(name = "EMAIL_ESTUDIANTE", nullable = false, length = 100)
    private String emailEstudiante;

    @Column(name = "FECHA_INSCRIPCION", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(name = "TOTAL_PAGAR", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "INSCRIPCION_CURSOS",
        joinColumns = @JoinColumn(name = "INSCRIPCION_ID"),
        inverseJoinColumns = @JoinColumn(name = "CURSO_ID")
    )
    private List<Curso> cursos;

    @PrePersist
    public void prePersist() {
        this.fechaInscripcion = LocalDateTime.now();
    }
}
