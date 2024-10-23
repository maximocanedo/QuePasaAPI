package frgp.utn.edu.ar.quepasa.service.geo;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityUpdateRequest;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CityService {
    // Buscar ciudades según query
    Page<City> search(String q, Pageable pageable, boolean active);

    // Obtener todas las ciudades
    List<City> getAll(boolean activeOnly);

    // Obtener una ciudad por su ID
    City getById(long id, boolean activeOnly);

    // Obtener ciudades según país
    List<City> getByCountry(String iso3);

    // Obtener ciudades según división subnacional
    List<City> getBySubnationalDivision(String iso3);

    // Crear ciudad
    City create(CityUpdateRequest request);

    // Actualizar ciudad
    City update(long id, CityUpdateRequest request);

    // Eliminar ciudad
    void delete(long id);
}
