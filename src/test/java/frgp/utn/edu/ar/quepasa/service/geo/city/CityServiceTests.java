package frgp.utn.edu.ar.quepasa.service.geo.city;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CityRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.geo.CityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Servicio de ciudades")
public class CityServiceTests {

    @MockBean
    private CityRepository cityRepository;
    @MockBean
    private CountryRepository countryRepository;
    @MockBean
    private SubnationalDivisionRepository subnationalDivisionRepository;
    @Autowired
    private CityService cityService;

    public CityServiceTests() { MockitoAnnotations.openMocks(this); }

    @Test
    @DisplayName("Buscar ciudad por ID.")
    public void findById_CityFound() {
        long id = 1L;

        setAuthContext();

        City city = new City();
        city.setId(1);
        city.setActive(true);

        when(cityRepository.findById(id)).thenReturn(Optional.of(city));

        assertDoesNotThrow(() -> {
            City foundCity = cityService.getById(id, true);
            assertNotNull(foundCity);
            assertEquals(id, foundCity.getId());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudad por ID, ID inexistente.")
    public void findById_CityNotFound() {
        long id = 1L;

        setAuthContext();

        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> cityService.getById(id, true));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades.")
    public void findCities() {
        setAuthContext();

        City city = new City();
        List<City> cities = new ArrayList<>(List.of(city));

        when(cityRepository.findAll()).thenReturn(cities);

        assertDoesNotThrow(() -> {
            List<City> foundCities = cityService.getAll(false);

            assertNotNull(foundCities);
            assertFalse(foundCities.isEmpty());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por país.")
    public void findByCountry_CountryFound() {
        String iso3 = "ARG";

        Country country = new Country();
        country.setIso3(iso3);

        City city = new City();
        List<City> cities = new ArrayList<>(List.of(city));

        setAuthContext();

        when(countryRepository.findByIso3(iso3)).thenReturn(Optional.of(country));
        when(cityRepository.findByCountry(iso3)).thenReturn(cities);

        assertDoesNotThrow(() -> {
            List<City> foundCities = cityService.getByCountry(iso3);

            assertNotNull(foundCities);
            assertFalse(foundCities.isEmpty());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por país, país inexistente.")
    public void findByCountry_CountryNotFound() {
        String iso3 = "ARG";

        setAuthContext();

        when(countryRepository.findByIso3(iso3)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> cityService.getByCountry(iso3));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por división subnacional.")
    public void findBySubdivision_SubdivisionFound() {
        String iso3 = "AR-C";

        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3);

        City city = new City();
        List<City> cities = new ArrayList<>(List.of(city));

        setAuthContext();

        when(subnationalDivisionRepository.findByIso3(iso3)).thenReturn(Optional.of(subdivision));
        when(cityRepository.findBySubdivision(subdivision)).thenReturn(cities);

        assertDoesNotThrow(() -> {
            List<City> foundCities = cityService.getBySubnationalDivision(iso3);

            assertNotNull(foundCities);
            assertFalse(foundCities.isEmpty());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar ciudades por división subnacional, división inexistente.")
    public void findBySubdivision_SubdivisionNotFound() {
        String iso3 = "AR-C";

        setAuthContext();

        when(subnationalDivisionRepository.findByIso3(iso3)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> cityService.getBySubnationalDivision(iso3));

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear ciudad.")
    public void createCity() {
        String iso3Country = "ARG";
        String iso3Sub = "AR-C";

        setAuthContext();

        Country country = new Country();
        country.setIso3(iso3Country);

        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3Sub);
        subdivision.setCountry(country);

        CityRequest request = new CityRequest();
        request.setName("Sandover Village");
        request.setSubdivision(iso3Sub);

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.of(country));
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.of(subdivision));

        assertDoesNotThrow(() -> {
            var saved = cityService.create(request);
            assertNotNull(saved);
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear ciudad, división subnacional inexistente.")
    public void createCity_SubdivisionNotFound() {
        String iso3Country = "ARG";
        String iso3Sub = "AR-C";

        setAuthContext();

        CityRequest request = new CityRequest();
        request.setName("Sandover Village");
        request.setSubdivision(iso3Sub);

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.empty());
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> cityService.create(request));

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar ciudad por ID.")
    public void updateCity() {
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

        CityRequest request = new CityRequest();
        request.setName("Rock Village");
        request.setSubdivision(iso3Sub);

        assertDoesNotThrow(() -> {
            var saved = cityService.update(id, request);
            assertNotNull(saved);
            assertEquals(id, saved.getId());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar ciudad por ID, ID inexistente.")
    public void updateCity_CityNotFound() {
        long id = 1L;

        setAuthContext();

        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        CityRequest request = new CityRequest();
        request.setName("Rock Village");

        assertThrows(Fail.class, () -> cityService.update(id, request));

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar ciudad por ID, división subnacional inexistente.")
    public void updateCity_SubdivisionNotFound() {
        long id = 1L;
        String iso3Country = "ARG";
        String iso3Sub = "AR-C";

        setAuthContext();

        City city = new City();
        city.setId(id);
        city.setName("Sandover Village");

        when(countryRepository.findByIso3(iso3Country)).thenReturn(Optional.empty());
        when(subnationalDivisionRepository.findByIso3(iso3Sub)).thenReturn(Optional.empty());
        when(cityRepository.findById(id)).thenReturn(Optional.of(city));

        CityRequest request = new CityRequest();
        request.setName("Rock Village");
        request.setSubdivision(iso3Sub);

        assertThrows(Fail.class, () -> cityService.update(id, request));

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar ciudad por ID.")
    public void deleteCity() {
        long id = 1L;

        setAuthContext();

        City city = new City();
        city.setId(id);

        when(cityRepository.findById(id)).thenReturn(Optional.of(city));

        assertDoesNotThrow(() -> cityService.delete(id));

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar ciudad por ID, ID inexistente.")
    public void deleteCity_CityNotFound() {
        long id = 1L;

        setAuthContext();

        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> cityService.delete(id));

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
