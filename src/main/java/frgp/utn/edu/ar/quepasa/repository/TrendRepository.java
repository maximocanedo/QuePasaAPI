package frgp.utn.edu.ar.quepasa.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import frgp.utn.edu.ar.quepasa.model.Trend;


public class TrendRepository {

    private static final String CALL_PROCEDURE = "{CALL contar_tendencias_tags_por_barrio(?, ?)}";

    public List<Trend> getTendencias(int neighbourhood, Timestamp datebase) {
        List<Trend> trends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://canedo.com.ar:3306/tif", "tusi", "K105T3R.RUL3S");
             CallableStatement statement = connection.prepareCall(CALL_PROCEDURE)) {

            statement.setInt(1, neighbourhood);
            statement.setTimestamp(2, datebase);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String tag = resultSet.getString("tag");
                    int cantidad = resultSet.getInt("cantidad");

                    trends.add(new Trend(tag, cantidad));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trends;
    }
}









