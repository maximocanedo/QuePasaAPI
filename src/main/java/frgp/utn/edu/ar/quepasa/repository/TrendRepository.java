package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.TrendProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TrendRepository {

    @Query(value = "CALL contar_tendencias_tags_por_barrio(:barrio, :fechaBase)", nativeQuery = true)
    List<TrendProjection> getTendencias(@Param("barrio") int barrio, @Param("fechaBase") Timestamp fechaBase);

}
