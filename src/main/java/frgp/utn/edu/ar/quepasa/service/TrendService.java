package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.repository.TrendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TrendService {

    @Autowired
    private TrendRepository trendRepository;

    /**
     * Devuelve las tendencias de un barrio en una fecha especifica.
     *
     * @param barrio el identificador del barrio.
     * @param fechaBase la fecha y hora en la que se quieren obtener las tendencias.
     * @return una lista de tendencias.
     */
    public List<Trend> getTendencias(int barrio, Timestamp fechaBase) {
        return trendRepository.getTendencias(barrio, fechaBase);
    }
    
}
