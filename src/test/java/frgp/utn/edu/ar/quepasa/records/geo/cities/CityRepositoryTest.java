package frgp.utn.edu.ar.quepasa.records.geo.cities;

import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CityRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SubnationalDivisionRepository subnationalDivisionRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("Alta, Borrado, Modificación y Lectura de ciudades")
    public void crudTest() {
        String iso3Country = "ARG";
        Country country = new Country();
        country.setIso3(iso3Country);
        country.setLabel("Argentina");
        country = countryRepository.saveAndFlush(country);
        assertNotNull(country, "No guardó el país de prueba. ");

        String iso3Subdivision = "AR-C";
        SubnationalDivision subdivision = new SubnationalDivision();
        subdivision.setIso3(iso3Subdivision);
        subdivision.setLabel("Buenos Aires");
        subdivision.setCountry(country);
        subdivision = subnationalDivisionRepository.saveAndFlush(subdivision);
        assertNotNull(subdivision, "No guardó la división subnacional de prueba. ");

        long id = 1;
        City city = new City();
        city.setId(id);
        city.setName("Tigre");
        city.setSubdivision(subdivision);
        city = cityRepository.saveAndFlush(city);
        assertNotNull(city, "No guardó la ciudad de prueba. ");
        assertTrue(cityRepository.findById(id).isPresent(), "No encuentra la ciudad recién creada. ");
        city.setName("San Fernando");
        assertNotNull(cityRepository.saveAndFlush(city), "No realizó la modificación. ");
        cityRepository.delete(city);
        assertTrue(cityRepository.findById(id).isEmpty(), "Encuentra una ciudad físicamente borrada. ");
    }
}
