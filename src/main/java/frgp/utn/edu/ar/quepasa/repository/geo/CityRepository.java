package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

}
