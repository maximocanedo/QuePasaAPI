package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Trend;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TrendRepository {

    /**
     * Obtiene las tendencias para un barrio en particular desde una fecha base especificada.
     * 
     * @param barrio el numero de barrio para el que se quieren obtener las tendencias.
     * @param fechaBase la fecha desde la que se contabilizan las etiquetas, en formato Timestamp.
     * @return una lista de tendencias, donde cada tendencia est  representada por un
     *     objeto Trend con la etiqueta y la cantidad de veces que ha sido etiquetada.
     */
    @Procedure(name = "contar_tendencias_tags_por_barrio")
    List<Trend> getTendencias(@Param("barrio") int barrio, @Param("fechaBase") Timestamp fechaBase);
}
