package frgp.utn.edu.ar.quepasa.service.geo.impl;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.geo.NeighbourhoodService;


@Service
public class NeighbourhoodServiceImpl implements NeighbourhoodService {

    private final NeighbourhoodRepository neighbourhoodRepository;
    private static final Logger logger = LoggerFactory.getLogger(NeighbourhoodServiceImpl.class);

    @Autowired
    public NeighbourhoodServiceImpl(NeighbourhoodRepository neighbourhoodRepository) {
        this.neighbourhoodRepository = neighbourhoodRepository;
    }

    // Crear un nuevo barrio
    @Override
    public Neighbourhood createNeighbourhood(Neighbourhood neighbourhood) {
        return neighbourhoodRepository.save(neighbourhood);
    }

    // Obtener todos los barrios
    @Override
    public Page<Neighbourhood> getAllNeighbourhoods(boolean activeOnly, Pageable pageable) {
        if (activeOnly) {
            return neighbourhoodRepository.findByActiveTrue(pageable);
        }
        return neighbourhoodRepository.findAll(pageable);
    }

    // Obtener un barrio por su ID
    @Override
    public Optional<Neighbourhood> getNeighbourhoodById(long id, boolean activeOnly) {
        if (activeOnly) {
            return neighbourhoodRepository.findActiveNeighbourhoodById(id);
        }
        return neighbourhoodRepository.findById(id);
    }

    // Buscar barrios por nombre
    @Override
    public Page<Neighbourhood> searchNeighbourhoodsByName(String name, Pageable pageable, long city) {
        logger.info("searchNeighbourhoodsByName: name={}, city={}, pageable={}", name, city, pageable);
    
        Page<Neighbourhood> result;
        if (city == -1) {
            logger.debug("sin filtro:");
            result = neighbourhoodRepository.findByNameAndActive(name, pageable);
        } else {
            logger.debug("con filtrp: city={}", city);
            result = neighbourhoodRepository.findByNameAndActive(name, pageable, city);
        }
        logger.info("{} barrios encontrados", result.getTotalElements());
        return result;
    }
    

    // Actualizar un barrio existente
    @Override
    public Neighbourhood updateNeighbourhood(Neighbourhood updatedNeighbourhood) {
        return neighbourhoodRepository.save(updatedNeighbourhood);
    }

    // Eliminar un barrio
    @Override
    public void deleteNeighbourhood(long id) {
        Optional<Neighbourhood> neighbourhoodOptional = neighbourhoodRepository.findById(id);
        if (neighbourhoodOptional.isPresent()) {
            Neighbourhood neighbourhood = neighbourhoodOptional.get();
            neighbourhood.setActive(false);
            neighbourhoodRepository.save(neighbourhood);
        }
    }
}
