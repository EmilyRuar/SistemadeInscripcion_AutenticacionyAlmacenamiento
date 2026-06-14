package com.duoc.inscripcion;

import com.duoc.inscripcion.dto.*;
import com.duoc.inscripcion.model.Curso;
import com.duoc.inscripcion.repository.CursoRepository;
import com.duoc.inscripcion.service.CursoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso cursoEjemplo;

    @BeforeEach
    void setUp() {
        cursoEjemplo = Curso.builder()
                .id(1L)
                .nombre("Spring Boot Avanzado")
                .instructor("Ana González")
                .duracion("40 horas")
                .costo(new BigDecimal("150000"))
                .build();
    }

    @Test
    @DisplayName("listarCursos retorna lista con todos los cursos")
    void listarCursos_retornaListaCompleta() {
        when(cursoRepository.findAll()).thenReturn(List.of(cursoEjemplo));

        List<CursoResponseDTO> resultado = cursoService.listarCursos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Spring Boot Avanzado");
        assertThat(resultado.get(0).getCosto()).isEqualByComparingTo("150000");
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("crearCurso persiste y retorna el curso creado")
    void crearCurso_persisteYRetorna() {
        CursoRequestDTO request = CursoRequestDTO.builder()
                .nombre("Docker para Desarrolladores")
                .instructor("Carlos Pérez")
                .duracion("20 horas")
                .costo(new BigDecimal("80000"))
                .build();

        Curso cursoGuardado = Curso.builder()
                .id(2L)
                .nombre(request.getNombre())
                .instructor(request.getInstructor())
                .duracion(request.getDuracion())
                .costo(request.getCosto())
                .build();

        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoGuardado);

        CursoResponseDTO resultado = cursoService.crearCurso(request);

        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getNombre()).isEqualTo("Docker para Desarrolladores");
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }
}
