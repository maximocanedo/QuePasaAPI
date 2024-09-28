package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeighbourhoodRepository extends JpaRepository<Neighbourhood, Long> {

}
