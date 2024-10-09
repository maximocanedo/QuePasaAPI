package frgp.utn.edu.ar.quepasa.config;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final SubnationalDivisionRepository subnationalDivisionRepository;
    private final CityRepository cityRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializator(
            UserRepository userRepository,
            CountryRepository countryRepository,
            SubnationalDivisionRepository subnationalDivisionRepository,
            CityRepository cityRepository,
            NeighbourhoodRepository neighbourhoodRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.subnationalDivisionRepository = subnationalDivisionRepository;
        this.cityRepository = cityRepository;
        this.neighbourhoodRepository = neighbourhoodRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {
        Country argentina = new Country();
        argentina.setIso3("ARG");
        argentina.setLabel("República Argentina");
        argentina.setActive(true);
        SubnationalDivision bsAsProv = new SubnationalDivision();
        bsAsProv.setIso3("AR-B");
        bsAsProv.setLabel("Buenos Aires");
        bsAsProv.setActive(true);
        bsAsProv.setCountry(argentina);
        bsAsProv.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        City tigre = new City();
        tigre.setName("Tigre");
        tigre.setSubdivision(bsAsProv);
        tigre.setActive(true);
        Neighbourhood rincon = new Neighbourhood();
        rincon.setName("Rincón de Milberg");
        rincon.setActive(true);
        rincon.setCity(tigre);
        User rootUser = new User();
        rootUser.setUsername("root");
        rootUser.setName("Administrador del sistema");
        rootUser.setAddress("Android Bv. 1223");
        rootUser.setPassword(passwordEncoder.encode("123456789"));
        rootUser.setRole(Role.ADMIN);
        rootUser.setActive(true);

        rootUser.setNeighbourhood(rincon);
        if (userRepository.findByUsername("root").isEmpty()) {
            countryRepository.save(argentina);
            subnationalDivisionRepository.save(bsAsProv);
            cityRepository.save(tigre);
            neighbourhoodRepository.save(rincon);

            userRepository.saveAndFlush(rootUser);
            System.out.println("Usuario 'root' creado.");
        } else {
            //userRepository.delete(rootUser);
            //userRepository.save(rootUser);
        }
    }

}
