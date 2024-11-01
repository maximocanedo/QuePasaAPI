package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quepasa.api.verifiers.geo.CountryVerifier;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, String>, CountryVerifier {

    Optional<Country> findByIso3(String iso3);

    @Override boolean existsByIso3(String iso3);

    @Query("SELECT p FROM Country p WHERE p.iso3 LIKE %:q% OR p.label LIKE %:q% AND p.active")
    Page<Country> search(String q, Pageable pageable);

}
