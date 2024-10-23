package main.java.frgp.utn.edu.ar.quepasa.service;
import frgp.utn.edu.ar.quepasa.repository.TrendRepository;

public class TrendService {

    private TrendRepository trendRepository = new TrendRepository();

    /**
     * Muestra las tendencias de un barrio en una fecha y hora determinadas.
     *
     * @param neighbourhood Identificador del barrio.
     * @param dateBase Fecha y hora de la consulta.
     */
    public void mostrarTendencias(int neighbourhood, Timestamp dateBase) {
        List<Trend> tendencias = trendRepository.getTendencias(neighbourhood, dateBase);

        for (Trend trend : tendencias) {
            System.out.println(trend);
        }
    }
}
