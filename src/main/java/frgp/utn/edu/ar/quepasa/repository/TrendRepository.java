package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Trend;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrendRepository {

    private static final String CALL_PROCEDURE = "{CALL contar_tendencias_tags_por_barrio(?, ?)}";

    /**
     * Obtiene las tendencias (tags) para un barrio en particular. La tendencia se
     * calcula contando la cantidad de veces que ha sido etiquetada en las publicaciones
     * realizadas en ese barrio desde la fecha base especificada.
     *
     * @param barrio el n mero de barrio para el que se quieren obtener las tendencias.
     * @param fechaBase la fecha desde la que se contabilizan las etiquetas.
     * @return una lista de tendencias, donde cada tendencia est  representada por un
     *     objeto Trend con la etiqueta y la cantidad de veces que ha sido etiquetada.
     */
    public List<Trend> getTendencias(int barrio, Timestamp fechaBase) {
        List<Trend> tendencias = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://canedo.com.ar:3306/tif", "tusi", "K105T3R.RUL3S");
             CallableStatement statement = connection.prepareCall(CALL_PROCEDURE)) {
            statement.setInt(1, barrio);
            statement.setTimestamp(2, fechaBase);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String tag = resultSet.getString("tag");
                    int cantidad = resultSet.getInt("cantidad");
                    tendencias.add(new Trend(tag, cantidad));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tendencias;
    }
}
