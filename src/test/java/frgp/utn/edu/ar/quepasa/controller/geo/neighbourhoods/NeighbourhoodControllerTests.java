package frgp.utn.edu.ar.quepasa.controller.geo.neighbourhoods;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import frgp.utn.edu.ar.quepasa.controller.geo.NeighbourhoodController;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.service.geo.NeighbourhoodService;
import jakarta.transaction.Transactional;
@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Tests para NeighbourhoodController")
public class NeighbourhoodControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private NeighbourhoodService neighbourhoodService;
    @SuppressWarnings("unused")
    private NeighbourhoodController neighbourhoodController;

    @BeforeAll
    public void setup() {
        neighbourhoodService = Mockito.mock(NeighbourhoodService.class);
        neighbourhoodController = new NeighbourhoodController(neighbourhoodService);
    }

    @Test
    @DisplayName("Crear un barrio")
    public void createNeighbourhoodTest() throws Exception {
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(1L);
        neighbourhood.setName("Barrio Norte");

        when(neighbourhoodService.createNeighbourhood(any(Neighbourhood.class))).thenReturn(neighbourhood);

        mockMvc.perform(post("/api/neighbourhoods")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(neighbourhood)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Barrio Norte"));
    }

    @Test
    @DisplayName("Obtener todos los barrios")
    public void getAllNeighbourhoodsTest() throws Exception {
        Neighbourhood neighbourhood1 = new Neighbourhood();
        neighbourhood1.setName("Rincón de Milberg");
    
        Neighbourhood neighbourhood2 = new Neighbourhood();
        neighbourhood2.setName("Tigre Centro");
    
        Pageable pageable = PageRequest.of(0, 10);
    
        Page<Neighbourhood> neighbourhoodsPage = new PageImpl<>(Arrays.asList(neighbourhood1, neighbourhood2), pageable, 2);
    
        when(neighbourhoodService.getAllNeighbourhoods(true, pageable)).thenReturn(neighbourhoodsPage);
    
        mockMvc.perform(get("/api/neighbourhoods?activeOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Rincón de Milberg"))
                .andExpect(jsonPath("$.content[1].name").value("Tigre Centro"));
    }
    
    @Test
    @DisplayName("Obtener un barrio por ID")
    public void getNeighbourhoodByIdTest() throws Exception {
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(1L);
        neighbourhood.setName("Recoleta");

        when(neighbourhoodService.getNeighbourhoodById(1L, true)).thenReturn(Optional.of(neighbourhood));

        mockMvc.perform(get("/api/neighbourhoods/1?activeOnly=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tigre Centro"));
    }

    @Test
    @DisplayName("Buscar barrio por nombre")
    public void searchNeighbourhoodByNameTest() throws Exception {
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setName("Belgrano");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Neighbourhood> neighbourhoodsPage = new PageImpl<>(List.of(neighbourhood));

        when(neighbourhoodService.searchNeighbourhoodsByName("Belgrano", pageable, -1)).thenReturn(neighbourhoodsPage);

        mockMvc.perform(get("/api/neighbourhoods/search?name=Belgrano"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Belgrano"));
    }


    @Test
    @DisplayName("Actualizar un barrio")
    public void updateNeighbourhoodTest() throws Exception {
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(1L);
        neighbourhood.setName("Retiro");

        when(neighbourhoodService.getNeighbourhoodById(1L, false)).thenReturn(Optional.of(neighbourhood));
        when(neighbourhoodService.updateNeighbourhood(any(Neighbourhood.class))).thenReturn(neighbourhood);

        mockMvc.perform(put("/api/neighbourhoods/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(neighbourhood)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Retiro"));
    }

    @Test
    @DisplayName("Eliminar un barrio")
    public void deleteNeighbourhoodTest() throws Exception {
        mockMvc.perform(delete("/api/neighbourhoods/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Buscar barrios por ciudad sin nombre especificado")
    public void searchNeighbourhoodsByCityTest() throws Exception {
        Neighbourhood neighbourhood1 = new Neighbourhood();
        neighbourhood1.setName("Palermo");
    
        Neighbourhood neighbourhood2 = new Neighbourhood();
        neighbourhood2.setName("Palermo Chico");
    
        Pageable pageable = PageRequest.of(0, 10);
        Page<Neighbourhood> neighbourhoodsPage = new PageImpl<>(Arrays.asList(neighbourhood1, neighbourhood2), pageable, 2);
    
        when(neighbourhoodService.searchNeighbourhoodsByName("", pageable, 56)).thenReturn(neighbourhoodsPage);
    
        mockMvc.perform(get("/api/neighbourhoods/search")
                .param("name", "")
                .param("page", "0")
                .param("size", "10")
                .param("city", "56"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Palermo"))
                .andExpect(jsonPath("$.content[1].name").value("Palermo Chico"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }
    
}
