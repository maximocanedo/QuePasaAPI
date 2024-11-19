package frgp.utn.edu.ar.quepasa.controller.geo;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.service.geo.NeighbourhoodService;

@RestController
@RequestMapping("/api/neighbourhoods")
public class NeighbourhoodController {
    private static final Logger logger = LoggerFactory.getLogger(NeighbourhoodController.class);

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
    public Page<Neighbourhood> searchNeighbourhoodsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "-1") long city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    
        try {
            logger.info("Iniciando búsqueda de vecindarios.");
            Pageable pageable = PageRequest.of(page, size);
            return neighbourhoodService.searchNeighbourhoodsByName(name, pageable, city);
        } catch (Exception e) {
            logger.error("Error durante la búsqueda de vecindarios: {}", e.getMessage(), e);
            throw e;
        }
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
