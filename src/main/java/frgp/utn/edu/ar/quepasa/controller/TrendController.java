package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.service.impl.TrendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trends")
public class TrendController {

    private final TrendServiceImpl trendService;

    @Autowired
    public TrendController(TrendServiceImpl trendService) {
        this.trendService = trendService;
    }

    /**
     * Devuelve las tendencias (tags) para un barrio en particular. La tendencia se
     * calcula contando la cantidad de veces que ha sido etiquetada en las publicaciones
     * realizadas en ese barrio desde la fecha base especificada.
     *
     * @param barrio el número de barrio para el que se quieren obtener las tendencias.
     * @param fechaBase la fecha desde la que se contabilizan las etiquetas, en formato ISO.
     * @return una lista de tendencias, donde cada tendencia está representada por un
     *     objeto Trend con la etiqueta y la cantidad de veces que ha sido etiquetada.
     */
    @GetMapping("/{barrio}")
    public ResponseEntity<List<Trend>> getTendencias(
            @PathVariable("barrio") int barrio,
            @RequestParam("fechaBase") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaBase
    ) {
        try {
            Timestamp fechaBaseTimestamp = Timestamp.valueOf(fechaBase);
            List<Trend> tendencias = trendService.getTrends(barrio, fechaBaseTimestamp);

            if (tendencias.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(tendencias);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(new Trend("Error en fechaBase: formato incorrecto", 0)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}





