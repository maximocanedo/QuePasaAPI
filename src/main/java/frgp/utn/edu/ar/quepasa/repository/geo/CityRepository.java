package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE (c.name LIKE %:query% OR c.subdivision.iso3 LIKE %:query%) AND c.active = :active")
    Page<City> search(@NotNull String query, @NotNull Pageable pageable, boolean active);

    List<City> findBySubdivision(SubnationalDivision subdivision);
}
