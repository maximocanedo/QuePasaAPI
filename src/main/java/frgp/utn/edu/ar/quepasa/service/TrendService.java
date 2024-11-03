package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Trend;

import java.sql.Timestamp;
import java.util.List;

public interface TrendService {
    List<Trend> getTrends(int neighborhood, String baseDate);
}
