package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trends")
public class TrendController {

    @Autowired
    private TrendService trendService;

    /**
     * Devuelve las tendencias (tags) para un barrio en particular. La tendencia se
     * calcula contando la cantidad de veces que ha sido etiquetada en las publicaciones
     * realizadas en ese barrio desde la fecha base especificada.
     *
     * @param barrio el n mero de barrio para el que se quieren obtener las tendencias.
     * @param fechaBase la fecha desde la que se contabilizan las etiquetas, en formato ISO.
     * @return una lista de tendencias, donde cada tendencia est  representada por un
     *     objeto Trend con la etiqueta y la cantidad de veces que ha sido etiquetada.
     */
    @GetMapping("/{barrio}")
    public List<Trend> getTendencias(
            @PathVariable("barrio") int barrio,
            @RequestParam("fechaBase") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaBase
    ) {
        Timestamp fechaBaseTimestamp = Timestamp.valueOf(fechaBase);

        return trendService.getTendencias(barrio, fechaBaseTimestamp);
    }
}






