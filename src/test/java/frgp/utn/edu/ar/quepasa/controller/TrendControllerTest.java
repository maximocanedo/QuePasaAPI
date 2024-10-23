package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.service.TrendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
//
import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.repository.TrendRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;



//
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controlador de tendencias")
public class TrendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrendService trendService;

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

        tendencias = Arrays.asList(
            new Trend("ejemplo", 5),
            new Trend("requestbody", 3)
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
        when(trendService.getTendencias(1, Timestamp.valueOf("2024-10-01 09:30:00")))
                .thenReturn(tendencias);

        mockMvc.perform(get("/api/trends/1")
                .param("fechaBase", "2024-10-01T09:30:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].tag", is("ejemplo")))
                .andExpect(jsonPath("$[0].cantidad", is(5)))
                .andExpect(jsonPath("$[1].tag", is("requestbody")))
                .andExpect(jsonPath("$[1].cantidad", is(3)));
    }
}

