package frgp.utn.edu.ar.quepasa.service.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;

import java.util.List;
import java.util.Optional;

public interface NeighbourhoodService {
    // Crear un nuevo barrio
    Neighbourhood createNeighbourhood(Neighbourhood neighbourhood);

    // Obtener todos los barrios
    List<Neighbourhood> getAllNeighbourhoods(boolean activeOnly);

    // Obtener un barrio por su ID
    Optional<Neighbourhood> getNeighbourhoodById(long id, boolean activeOnly);

    // Buscar barrios por nombre
    List<Neighbourhood> searchNeighbourhoodsByName(String name);

    // Actualizar un barrio existente
    Neighbourhood updateNeighbourhood(Neighbourhood updatedNeighbourhood);

    // Eliminar un barrio
    void deleteNeighbourhood(long id);
}
