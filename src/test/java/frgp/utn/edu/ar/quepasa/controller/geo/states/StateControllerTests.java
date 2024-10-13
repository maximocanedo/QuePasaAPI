package frgp.utn.edu.ar.quepasa.controller.geo.states;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.model.enums.SubnationalDivisionDenomination;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.geo.impl.SubnationalDivisionServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StateControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private SubnationalDivisionRepository repository;
    private CountryRepository countries;
    private SubnationalDivisionServiceImpl service;
    @Autowired private ObjectMapper objectMapper;

    public StateControllerTests() {
        repository = Mockito.mock(SubnationalDivisionRepository.class);
        countries = Mockito.mock(CountryRepository.class);
        service = new SubnationalDivisionServiceImpl(repository, countries);
    }

    @BeforeAll
    public void setup() {

    }


    @Test
    @WithMockUser(username = "root", roles = "ADMIN")
    @DisplayName("Crear registro de estado")
    public void create() throws Exception {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(argentina);
        file.setIso3("AR-URU");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URU")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        when(repository.save(any())).thenReturn(file);


        mockMvc.perform(post("/api/states")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(file))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.iso3").value("AR-URU"));


    }

}

