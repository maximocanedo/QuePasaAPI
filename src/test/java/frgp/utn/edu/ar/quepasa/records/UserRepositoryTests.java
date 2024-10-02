package frgp.utn.edu.ar.quepasa.records;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.enums.SubnationalDivisionDenomination;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired private CountryRepository countryRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private SubnationalDivisionRepository subnationalDivisionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NeighbourhoodRepository neighbourhoodRepository;


    private Pageable firstPage() {
        return new Pageable() {
            @Override
            public int getPageNumber() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return 20;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return Sort.by(Sort.Direction.ASC, "username");
            }

            @Override
            public Pageable next() {
                return this;
            }

            @Override
            public Pageable previousOrFirst() {
                return this;
            }

            @Override
            public Pageable first() {
                return this;
            }

            @Override
            public Pageable withPage(int pageNumber) {
                return this;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };
    }

    @Test
    @DisplayName("Alta, Borrado, Modificación y Lectura de usuarios")
    public void crudTest() {
        Country ir = new Country();
        ir.setIso3("IR");
        ir.setLabel("Imperium Romanum");
        ir.setActive(true);
        assertNotNull(countryRepository.save(ir), "No guardó el país de testing.");
        SubnationalDivision sd = new SubnationalDivision();
        sd.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        sd.setIso3("IR-IT");
        sd.setLabel("Italia");
        sd.setCountry(ir);
        sd.setActive(true);
        assertNotNull(subnationalDivisionRepository.save(sd), "No guardó la entidad subnacional de testing. ");
        City city = new City();
        city.setSubdivision(sd);
        city.setName("Roma");
        city.setActive(true);
        assertNotNull(cityRepository.save(city), "No guardó la ciudad de testing. ");
        Neighbourhood n = new Neighbourhood();
        n.setName("Monti");
        n.setCity(city);
        n.setId(389420348);
        n.setActive(true);
        assertNotNull(neighbourhoodRepository.save(n), "No guardó el barrio de testing. ");
        User a = new User();
        a.setId(32423424);
        String tusername = "testUser00112";
        a.setUsername(tusername);
        a.setPassword("testPassword");
        a.setNeighbourhood(n);
        a.setRole(Role.USER);
        a.setAddress("Trevi 8704 E7 K-28");
        a.setName("Augustus X47000");
        a = userRepository.saveAndFlush(a);
        assertNotNull(a, "No guardó el usuario de prueba. ");
        assertTrue(userRepository.findByUsername(tusername).isPresent(), "No encuentra el usuario de prueba recién creado. ");
        Page<User> p = userRepository.search("Augustus X47000", firstPage(), true);
        assertNotNull(p, "Resultado de búsqueda es nulo. ");
        assertFalse(p.isEmpty(), "No encuentra ningún resultado tras crear el usuario de prueba. ");
        a.setName("Augustus E45000");
        assertNotNull(userRepository.saveAndFlush(a), "No realizó la modificación. ");
        userRepository.delete(a);
        assertTrue(userRepository.findByUsername(tusername).isEmpty(), "Encuentra un usuario físicamente borrado. ");
    }

}
