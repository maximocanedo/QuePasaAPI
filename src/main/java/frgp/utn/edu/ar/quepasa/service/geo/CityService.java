package frgp.utn.edu.ar.quepasa.service.geo;

import frgp.utn.edu.ar.quepasa.data.request.geo.CityRequest;
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
    Page<City> getAll(Pageable pageable, boolean activeOnly);

    // Obtener una ciudad por su ID
    City getById(long id, boolean activeOnly);

    // Obtener ciudades según país
    Page<City> getByCountry(String iso3, Pageable pageable);

    // Obtener ciudades según división subnacional
    Page<City> getBySubnationalDivision(String iso3, Pageable pageable);

    // Crear ciudad
    City create(CityRequest request);

    // Actualizar ciudad
    City update(long id, CityRequest request);

    // Eliminar ciudad
    void delete(long id);
}
