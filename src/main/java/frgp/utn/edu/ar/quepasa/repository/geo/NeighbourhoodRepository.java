package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NeighbourhoodRepository extends JpaRepository<Neighbourhood, Long> {

    @Query("SELECT n FROM Neighbourhood n WHERE n.id = :id AND n.active")
    Optional<Neighbourhood> findActiveNeighbourhoodById(long id);

}
