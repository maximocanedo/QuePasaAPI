package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quepasa.api.verifiers.geo.SubnationalDivisionVerifier;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubnationalDivisionRepository extends JpaRepository<SubnationalDivision, String>, SubnationalDivisionVerifier {

    @Query("SELECT s FROM SubnationalDivision s WHERE s.active = true AND s.iso3 = :iso3")
    Optional<SubnationalDivision> findByIso3(String iso3);

    @Override
    @Query("SELECT COUNT(s) > 0 FROM SubnationalDivision s WHERE s.active = true AND s.iso3 = :iso3")
    boolean existsByIso3(String iso3);

    @Query("SELECT s FROM SubnationalDivision s WHERE s.active = true AND s.country.iso3 = :countrysIso3")
    List<SubnationalDivision> getAllFrom(String countrysIso3);

}
