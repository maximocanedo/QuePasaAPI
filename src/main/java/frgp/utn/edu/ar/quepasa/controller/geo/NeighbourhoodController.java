package frgp.utn.edu.ar.quepasa.controller.geo;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.service.geo.NeighbourhoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/neighbourhoods")
public class NeighbourhoodController {

    private final NeighbourhoodService neighbourhoodService;

    @Autowired
    public NeighbourhoodController(NeighbourhoodService neighbourhoodService) {
        this.neighbourhoodService = neighbourhoodService;
    }

    // Crear un nuevo barrio
    @PostMapping
    public ResponseEntity<Neighbourhood> createNeighbourhood(@RequestBody Neighbourhood neighbourhood) {
        Neighbourhood createdNeighbourhood = neighbourhoodService.createNeighbourhood(neighbourhood);
        return ResponseEntity.ok(createdNeighbourhood);
    }

    // Obtener todos los barrios
    @GetMapping
    public Page<Neighbourhood> getAllNeighbourhoods(@RequestParam(defaultValue = "true") boolean activeOnly, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return neighbourhoodService.getAllNeighbourhoods(activeOnly, pageable);
    }

    // Obtener un barrio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Neighbourhood> getNeighbourhoodById(@PathVariable long id, @RequestParam(defaultValue = "true") boolean activeOnly) {
        Optional<Neighbourhood> neighbourhood = neighbourhoodService.getNeighbourhoodById(id, activeOnly);
        return neighbourhood.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar barrio por nombre
    @GetMapping("/search")
    public Page<Neighbourhood> searchNeighbourhoodsByName(@RequestParam String name, @RequestParam(defaultValue = "-1") int city, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return neighbourhoodService.searchNeighbourhoodsByName(name, pageable, city);
    }

    // Actualizar un barrio
    @PutMapping("/{id}")
    public ResponseEntity<Neighbourhood> updateNeighbourhood(@PathVariable long id, @RequestBody Neighbourhood updatedNeighbourhood) {
        Optional<Neighbourhood> existingNeighbourhood = neighbourhoodService.getNeighbourhoodById(id, false);
        if (existingNeighbourhood.isPresent()) {
            updatedNeighbourhood.setId(id);
            Neighbourhood updated = neighbourhoodService.updateNeighbourhood(updatedNeighbourhood);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un barrio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNeighbourhood(@PathVariable long id) {
        neighbourhoodService.deleteNeighbourhood(id);
        return ResponseEntity.noContent().build();
    }

}
