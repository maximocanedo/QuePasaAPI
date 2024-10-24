package frgp.utn.edu.ar.quepasa.controller.geo.cities;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.geo.CityUpdateRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CityRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
@DisplayName("Controlador de ciudades")
public class CityControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final SubnationalDivisionRepository subnationalDivisionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public CityControllerTests() {
        cityRepository = Mockito.mock(CityRepository.class);
        countryRepository = Mockito.mock(CountryRepository.class);
        subnationalDivisionRepository =  Mockito.mock(SubnationalDivisionRepository.class);
    }

    @BeforeAll
    public void setup() {

    }

    @Test
    @DisplayName("Buscar ciudad por ID.")
    public void searchCityById() throws Exception {
        setAuthContext();

        when(cityRepository.findById(1L)).thenReturn(Optional.of(new City()));

        mockMvc.perform((get("/api/cities/{id}", 1L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1L));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudad por ID. ID inexistente.")
    public void searchCityById_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/cities/{id}", 909L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("City not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades.")
    public void searchCities() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/cities/all"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por país.")
    public void searchCitiesByCountry() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/cities/country/{iso3}", "ARG"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por país, país inexistente.")
    public void searchCitiesByCountry_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/cities/country/{iso3}", "ASD"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por división subnacional.")
    public void searchCitiesBySubdivision() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/cities/subdivision/{iso3}", "AR-C"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por división subnacional, división inexistente.")
    public void searchCitiesBySubdivision_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/cities/subdivision/{iso3}", "ASD"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear ciudad.")
    public void createCity() throws Exception {
        String iso3Country = "ARG";
        String iso3Sub = "AR-C";

        setAuthContext();

        Country country = new Country();
        country.setIso3(iso3Country);

        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3Sub);
        subdivision.setCountry(country);

        CityUpdateRequest request = new CityUpdateRequest();
        request.setName("Sandover Village");
        request.setSubdivision(iso3Sub);

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.of(country));
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.of(subdivision));

        mockMvc.perform((post("/api/cities"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear ciudad, división subnacional inexistente.")
    public void createCity_SubdivisionNotFound() throws Exception {
        String iso3Country = "ARG";
        String iso3Sub = "AR-XYZ";

        setAuthContext();

        Country country = new Country();
        country.setIso3(iso3Country);

        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3Sub);
        subdivision.setCountry(country);

        CityUpdateRequest request = new CityUpdateRequest();
        request.setName("Sandover Village");
        request.setSubdivision(iso3Sub);

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.of(country));
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.of(subdivision));

        mockMvc.perform((post("/api/cities"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar ciudad por ID.")
    public void updateCity() throws Exception {
        long id = 1L;
        String iso3Country = "ARG";
        String iso3Sub = "AR-C";

        setAuthContext();

        Country country = new Country();
        country.setIso3(iso3Country);

        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3Sub);
        subdivision.setCountry(country);

        City city = new City();
        city.setId(id);
        city.setName("Sandover Village");
        city.setSubdivision(subdivision);

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.of(country));
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.of(subdivision));
        when(cityRepository.findById(id)).thenReturn(Optional.of(city));

        CityUpdateRequest request = new CityUpdateRequest();
        request.setName("Rock Village");
        request.setSubdivision(iso3Sub);

        mockMvc.perform((patch("/api/cities/{id}", 1L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar ciudad por ID, ID inexistente.")
    public void updateCity_NotFound() throws Exception {
        long id = 1L;

        setAuthContext();

        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        CityUpdateRequest request = new CityUpdateRequest();
        request.setName("Rock Village");

        mockMvc.perform((patch("/api/cities/{id}", 909L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar ciudad por ID, división subnacional inexistente.")
    public void updateCity_SubdivisionNotFound() throws Exception {
        long id = 1L;
        String iso3Country = "ARG";
        String iso3Sub = "AR-XYZ";

        setAuthContext();

        Country country = new Country();
        country.setIso3(iso3Country);

        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3Sub);
        subdivision.setCountry(country);

        City city = new City();
        city.setId(id);
        city.setName("Sandover Village");
        city.setSubdivision(subdivision);

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.of(country));
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.of(subdivision));
        when(cityRepository.findById(id)).thenReturn(Optional.of(city));

        CityUpdateRequest request = new CityUpdateRequest();
        request.setName("Rock Village");
        request.setSubdivision(iso3Sub);

        mockMvc.perform((patch("/api/cities/{id}", 1L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar ciudad por ID.")
    public void deleteCity() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/cities/{id}", 1L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar ciudad por ID, ID inexistente.")
    public void deleteCity_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/cities/{id}", 909L))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    private void setAuthContext() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("root")
                .password("123456789")
                .roles("ADMIN")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthContext() {
        SecurityContextHolder.clearContext();
    }

}
