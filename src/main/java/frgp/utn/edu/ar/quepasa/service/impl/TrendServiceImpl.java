package frgp.utn.edu.ar.quepasa.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/* 
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;*/
import quepasa.api.exception.Fail;
import quepasa.api.exception.ValidationError;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.repository.TrendRepository;
import frgp.utn.edu.ar.quepasa.service.TrendService;


@Service("trendService")
public class TrendServiceImpl implements TrendService {
    private final TrendRepository trendRepository;

    @Autowired
    public TrendServiceImpl(TrendRepository trendRepository) {
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
    @Override
    public List<Trend> getTrends(int barrio, String fechaBase) {
        if (fechaBase == null) {
            Set<String> errors = new HashSet<>();
            errors.add("La fecha base no puede ser nula o con formato incorrecto.");
            throw new ValidationError("fechaBase", errors);
        }
    
        try {
            List<Trend> tendencias = trendRepository.getTendencias(barrio, fechaBase);
    
            if (tendencias == null || tendencias.isEmpty()) {
                throw new Fail("No se encontraron tendencias para el barrio especificado.");
            }
    
            return tendencias;
    
        } catch (IllegalArgumentException e) {
            Set<String> errors = new HashSet<>();
            errors.add("Formato de fecha incorrecto: " + e.getMessage());
            throw new ValidationError("fechaBase", errors);
        } catch (Fail e) {
            throw new Fail("Error interno del servidor: " + e.getMessage());
        }
    }
    }
