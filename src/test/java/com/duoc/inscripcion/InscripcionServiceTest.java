package com.duoc.inscripcion;

import com.duoc.inscripcion.dto.*;
import com.duoc.inscripcion.model.*;
import com.duoc.inscripcion.repository.InscripcionRepository;
import com.duoc.inscripcion.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private CursoService cursoService;

    @InjectMocks
    private InscripcionService inscripcionService;

    @Test
    @DisplayName("inscribir calcula el total correctamente y persiste la inscripción")
    void inscribir_calculaTotalYPersiste() {
        Curso c1 = Curso.builder().id(1L).nombre("Spring Boot").instructor("Ana")
                .duracion("40h").costo(new BigDecimal("100000")).build();
        Curso c2 = Curso.builder().id(2L).nombre("Docker").instructor("Carlos")
                .duracion("20h").costo(new BigDecimal("80000")).build();

        InscripcionRequestDTO request = InscripcionRequestDTO.builder()
                .nombreEstudiante("María Soto")
                .emailEstudiante("maria@duoc.cl")
                .cursoIds(List.of(1L, 2L))
                .build();

        when(cursoService.buscarPorIds(List.of(1L, 2L))).thenReturn(List.of(c1, c2));

        Inscripcion guardada = Inscripcion.builder()
                .id(10L)
                .nombreEstudiante("María Soto")
                .emailEstudiante("maria@duoc.cl")
                .cursos(List.of(c1, c2))
                .totalPagar(new BigDecimal("180000"))
                .fechaInscripcion(LocalDateTime.now())
                .build();

        when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(guardada);

        InscripcionResponseDTO resultado = inscripcionService.inscribir(request);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getTotalPagar()).isEqualByComparingTo("180000");
        assertThat(resultado.getCursos()).hasSize(2);
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }
}
