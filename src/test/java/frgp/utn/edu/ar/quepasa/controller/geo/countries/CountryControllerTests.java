package frgp.utn.edu.ar.quepasa.controller.geo.countries;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CountryControllerTests {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Búsqueda general")
    public void testSearch() throws Exception {

        mockMvc.perform(get("/api/countries")
            .param("q", "")
            .param("page", "0")
            .param("size", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    @DisplayName("Búsqueda individual: Argentina")
    public void testFindOne() throws Exception {

        mockMvc.perform(get("/api/countries/ARG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iso3").exists())
                .andExpect(jsonPath("$.iso3").value("ARG"))
                .andExpect(jsonPath("$.label").exists())
                .andExpect(jsonPath("$.label").isNotEmpty());
    }

    @Test
    @DisplayName("Búsqueda individual: País no existente")
    public void testFindNotFound() throws Exception {

        mockMvc.perform(get("/api/countries/XOXJ"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.iso3").doesNotExist())
                .andExpect(jsonPath("$.label").doesNotExist());

    }

}
