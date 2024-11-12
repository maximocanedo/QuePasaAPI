package test.java.frgp.utn.edu.ar.quepasa.controller;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.service.impl.TrendServiceImpl;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controlador de tendencias")
public class TrendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrendServiceImpl trendService;

    private List<Trend> tendencias;

    /**
     * Configura el entorno de pruebas.
     *
     * - Inicializa el mockito.
     * - Crea una lista de tendencias de ejemplo.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Configuración de las tendencias para que coincidan con lo esperado en la BD
        tendencias = Arrays.asList(
            new Trend("ejemplo", 4),
            new Trend("requestbody", 4),
            new Trend("requestbody2", 4)
        );
    }

    /**
     * Verifica que se pueda obtener una lista de tendencias para un barrio
     * especifico y desde una fecha base especificada.
     * 
     * @throws Exception
     */
    @Test
    public void testGetTendencias() throws Exception {
        // Configuración del mock para que devuelva los datos esperados
        when(trendService.getTrends(1, ("2024-10-01")))
                .thenReturn(tendencias);

        // Ejecución y verificación de la solicitud
        mockMvc.perform(get("/api/trends/1")
                .param("fechaBase", "2024-10-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$[0].tag", is("ejemplo")))
                .andExpect(jsonPath("$[0].cantidad", is(4)))
                .andExpect(jsonPath("$[1].tag", is("requestbody")))
                .andExpect(jsonPath("$[1].cantidad", is(4)))
                .andExpect(jsonPath("$[2].tag", is("requestbody2")))
                .andExpect(jsonPath("$[2].cantidad", is(4)));
    }
}
