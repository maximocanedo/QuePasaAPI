package frgp.utn.edu.ar.quepasa.controller;

import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import frgp.utn.edu.ar.quepasa.model.Trend;
import frgp.utn.edu.ar.quepasa.service.impl.TrendServiceImpl;

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
     * @param fechaBase la fecha desde la que se contabilizan las etiquetas
     * @return una lista de tendencias, donde cada tendencia está representada por un
     *     objeto Trend con la etiqueta y la cantidad de veces que ha sido etiquetada.
     */
    @GetMapping("/{barrio}")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<List<Trend>> getTendencias(
            @PathVariable("barrio") int barrio,
            @RequestParam("fechaBase") String fechaBase
    ) {
        try {
            List<Trend> tendencias = trendService.getTrends(barrio, fechaBase);

            if (tendencias.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(tendencias);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(new Trend("Error en fechaBase: formato incorrecto", 0)));
        } catch (Exception e) {
            e.printStackTrace();
            // Manejo genérico de excepciones, puede mejorarse según el caso
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
