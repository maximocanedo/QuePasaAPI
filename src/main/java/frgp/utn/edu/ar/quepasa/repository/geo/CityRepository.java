package frgp.utn.edu.ar.quepasa.repository.geo;

import frgp.utn.edu.ar.quepasa.model.geo.City;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT e FROM Event e WHERE (e.name LIKE %:query% OR e.subdivision LIKE %:query%) AND e.active = :active")
    Page<City> search(@NotNull String query, @NotNull Pageable pageable, boolean active);
}
