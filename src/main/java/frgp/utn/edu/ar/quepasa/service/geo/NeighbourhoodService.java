package frgp.utn.edu.ar.quepasa.service.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NeighbourhoodService {
    // Crear un nuevo barrio
    Neighbourhood createNeighbourhood(Neighbourhood neighbourhood);

    // Obtener todos los barrios
    Page<Neighbourhood> getAllNeighbourhoods(boolean activeOnly, Pageable pageable);

    // Obtener un barrio por su ID
    Optional<Neighbourhood> getNeighbourhoodById(long id, boolean activeOnly);

    // Buscar barrios por nombre
    Page<Neighbourhood> searchNeighbourhoodsByName(String name, Pageable pageable, int city);

    // Actualizar un barrio existente
    Neighbourhood updateNeighbourhood(Neighbourhood updatedNeighbourhood);

    // Eliminar un barrio
    void deleteNeighbourhood(long id);
}
