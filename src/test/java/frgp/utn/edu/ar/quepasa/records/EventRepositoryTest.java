package frgp.utn.edu.ar.quepasa.records;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CityRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubnationalDivisionRepository subnationalDivisionRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;

    @Test
    @DisplayName("Alta, Borrado, Modificación y Lectura de Eventos")
    public void crudTest() {
        Country c = new Country();
        c.setIso3("ARG");
        c.setLabel("Argentina");
        c.setActive(true);
        c = countryRepository.saveAndFlush(c);
        assertNotNull(c, "No se pudo guardar el país de prueba");

        SubnationalDivision sd = new SubnationalDivision();
        sd.setCountry(c);
        sd.setIso3("BUE");
        sd.setLabel("Buenos Aires");
        sd.setActive(true);
        sd = subnationalDivisionRepository.saveAndFlush(sd);
        assertNotNull(sd, "No se pudo guardar la provincia de prueba");

        long cityId = 1L;
        City city = new City();
        city.setId(cityId);
        city.setSubdivision(sd);
        city.setName("La Plata");
        city.setActive(true);
        city = cityRepository.saveAndFlush(city);
        assertNotNull(city, "No se pudo guardar la ciudad de prueba");

        long neighbourhoodId = 1L;
        Neighbourhood n = new Neighbourhood();
        n.setId(neighbourhoodId);
        n.setCity(city);
        n.setName("Centro");
        n.setActive(true);
        n = neighbourhoodRepository.saveAndFlush(n);
        assertNotNull(n, "No se pudo guardar el barrio de prueba");

        Integer uId = 1;
        User u = new User();
        u.setId(uId);
        u.setUsername("test");
        u.setPassword("test");
        u.setName("test");
        u.setAddress("test 123");
        u.setNeighbourhood(n);
        u.setRole(Role.CONTRIBUTOR);
        u.setActive(true);
        u = userRepository.saveAndFlush(u);
        assertNotNull(u, "No se pudo guardar el usuario de prueba");

        Set<Neighbourhood> neighbourhoods = new HashSet<>();
        neighbourhoods.add(n);

        Event e = new Event();
        e.setOwner(u);
        e.setCategory(EventCategory.EDUCATIVE);
        e.setAudience(Audience.NEIGHBORHOOD);
        e.setTitle("Evento de prueba");
        e.setDescription("Descripción del evento de prueba");
        e.setAddress("Fake Street 234");
        e.setNeighbourhoods(neighbourhoods);
        e.setCreatedAt(Timestamp.valueOf("2024-10-21 13:41:03.123456789"));
        e.setStart(LocalDateTime.now());
        e.setEnd(LocalDateTime.now().plusHours(1));
        e.setActive(true);

        e = eventRepository.saveAndFlush(e);
        assertNotNull(e, "No se pudo guardar el evento de prueba");
        UUID eventId = e.getId();
        assertTrue(eventRepository.existsById(eventId), "No se pudo leer el evento de prueba");
        e.setAddress("Evento de prueba modificado");
        assertNotNull(eventRepository.saveAndFlush(e), "No se pudo modificar el evento de prueba");
        eventRepository.delete(e);
        assertTrue(eventRepository.findById(eventId).isEmpty(), "No se pudo borrar el evento de prueba");
    }
}
