package frgp.utn.edu.ar.quepasa.controller.geo.states;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.geo.SubnationalDivisionUpdateRequest;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controladores de entidades subnacionales")
public class StateControllerTests {
    @Autowired
    private MockMvc mockMvc;
    private final SubnationalDivisionRepository repository;
    private final CountryRepository countries;
    private SubnationalDivisionServiceImpl service;
    @Autowired
    private ObjectMapper objectMapper;

    public StateControllerTests() {
        repository = Mockito.mock(SubnationalDivisionRepository.class);
        countries = Mockito.mock(CountryRepository.class);
        service = new SubnationalDivisionServiceImpl(repository, countries);
    }

    @BeforeAll
    public void setup() {

    }

    @Test
    @DisplayName("Buscar provincias por país")
    public void search() throws Exception {
        when(repository.getAllFrom(argentina().getIso3())).thenReturn(List.of(santaFe(), chubut()));
        when(repository.getAllFrom(uruguay().getIso3())).thenReturn(List.of(soriano()));

        mockMvc.perform(get("/api/countries/ARG/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].iso3").exists());
    }

    @Test
    @DisplayName("Buscar provincia específica")
    public void searchOne() throws Exception {

        mockMvc.perform(get("/api/states/AR-U"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iso3").exists())
                .andExpect(jsonPath("$.iso3").value("AR-U"));
    }

    @Test
    @DisplayName("Crear registro de estado")
    public void create() throws Exception {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(argentina);
        file.setIso3("AR-URY");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URY")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        when(repository.save(any())).thenReturn(file);

        mockMvc.perform(post("/api/states")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(file))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.iso3").value("AR-URY"));
    }

    @Test
    @DisplayName("#40: Eliminar registro de estado")
    public void deleteState() throws Exception {
        when(repository.findByIso3(soriano().getIso3())).thenReturn(Optional.of(soriano()));
        mockMvc.perform(
                        delete("/api/states/" + soriano().getIso3())
                                .with(user("root").password("123456789").roles("ADMIN"))
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("#40: Eliminar registro de estado que no existe")
    public void deleteStateNotFound() throws Exception {
        when(repository.findByIso3(soriano().getIso3())).thenReturn(Optional.empty());
        mockMvc.perform(
                        delete("/api/states/KDSLJFSK")
                                .with(user("root").password("123456789").roles("ADMIN"))
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Editar registro de estado")
    public void update() throws Exception {
        when(repository.findByIso3(soriano().getIso3())).thenReturn(Optional.of(soriano()));
        var r = new SubnationalDivisionUpdateRequest();
        r.setLabel("Soriana");
        r.setCountry(argentina());
        r.setDenomination(SubnationalDivisionDenomination.TERRITORY);
        when(repository.existsByIso3(soriano().getIso3())).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        when(countries.existsByIso3("URY")).thenReturn(true);
        var upd = new SubnationalDivision();
        upd.setLabel(r.getLabel());
        upd.setCountry(r.getCountry());
        upd.setDenomination(r.getDenomination());
        when(repository.save(any())).thenReturn(upd);

        mockMvc.perform(
                patch("/api/states/" + soriano().getIso3())
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(r))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value(upd.getLabel()))
                .andExpect(jsonPath("$.country.iso3").value(upd.getCountry().getIso3()));

    }

    @Test
    @DisplayName("Crear registro de estado, código ISO 3166-2 inválido")
    public void createWithInvalidIso31662Code() throws Exception {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(argentina);
        file.setIso3("AR%·$%");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URU")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        when(repository.save(any())).thenReturn(file);

        mockMvc.perform(post("/api/states")
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(file))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.iso3").doesNotExist())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("iso3"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Crear registro de estado, nombre inválido")
    public void createWithInvalidName() throws Exception {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setCountry(argentina);
        file.setLabel("A$·%&ASDF");
        file.setIso3("AR-XXZ");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-XXZ")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        when(repository.save(any())).thenReturn(file);

        mockMvc.perform(post("/api/states")
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(file))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.iso3").doesNotExist())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("label"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Crear registro de estado, país no existente")
    public void createWithInvalidCountry() throws Exception {
        var uruguay = new Country();
        uruguay.setIso3("UYU");
        var file = new SubnationalDivision();
        file.setCountry(uruguay);
        file.setLabel("Departamento de Montevideo");
        file.setIso3("UY-XXZ");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-XXZ")).thenReturn(false);
        when(countries.existsByIso3("UYU")).thenReturn(false);
        when(repository.save(any())).thenReturn(file);

        mockMvc.perform(post("/api/states")
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(file))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.iso3").doesNotExist())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("country"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    public Country uruguay() {
        var uruguay = new Country();
        uruguay.setIso3("UYU");
        return uruguay;
    }

    public Country argentina() {
        var argentina = new Country();
        argentina.setIso3("ARG");
        return argentina;
    }

    public SubnationalDivision soriano() {
        var p1 = new SubnationalDivision();
        p1.setCountry(uruguay());
        p1.setLabel("Soriano");
        p1.setIso3("UY-SO");
        return p1;
    }

    public SubnationalDivision santaFe() {
        var p1 = new SubnationalDivision();
        p1.setCountry(argentina());
        p1.setLabel("Santa Fe");
        p1.setIso3("AR-S");
        return p1;
    }

    public SubnationalDivision chubut() {
        var p1 = new SubnationalDivision();
        p1.setCountry(argentina());
        p1.setLabel("Chubut");
        p1.setIso3("AR-U");
        return p1;
    }

}

