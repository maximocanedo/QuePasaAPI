package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

    Optional<Country> findByIso3(String iso3);

    boolean existsByIso3(String iso3);

    @Query("SELECT p FROM Country p WHERE p.iso3 LIKE %:q% OR p.label LIKE %:q% AND p.active")
    Page<Country> search(String q, Pageable pageable);

}
