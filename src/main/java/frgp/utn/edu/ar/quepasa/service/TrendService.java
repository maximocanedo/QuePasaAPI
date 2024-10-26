package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.repository.TrendRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TrendService {
    private final TrendRepository trendRepository;

    @Autowired
    public TrendService(TrendRepository trendRepository) {
        this.trendRepository = trendRepository;
    }

    /**
     * Obtiene las tendencias para un barrio en particular desde una fecha base especificada.
     * 
     * @param barrio el numero de barrio para el que se quieren obtener las tendencias.
     * @param fechaBase la fecha desde la que se contabilizan las etiquetas, en formato Timestamp.
     * @return una lista de tendencias, donde cada tendencia est  representada por un
     *     objeto Trend con la etiqueta y la cantidad de veces que ha sido etiquetada.
     */
    public List<Trend> getTendencias(int barrio, Timestamp fechaBase) {
        return trendRepository.getTendencias(barrio, fechaBase);
    }

    
}
