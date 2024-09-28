package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

    Optional<Country> findByIso3(String iso3);

}
