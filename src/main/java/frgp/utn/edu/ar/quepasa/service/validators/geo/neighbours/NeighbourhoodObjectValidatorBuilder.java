package frgp.utn.edu.ar.quepasa.service.validators.geo.neighbours;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.Optional;

public class NeighbourhoodObjectValidatorBuilder extends ValidatorBuilder<Neighbourhood> {

    public NeighbourhoodObjectValidatorBuilder(Neighbourhood value) {
        super(value, "neighbourhood");
    }

    public NeighbourhoodObjectValidatorBuilder isActive(NeighbourhoodRepository repository) {
        Optional<Neighbourhood> optional = repository.findById(getValue().getId());
        if(optional.isPresent()) {
            if(!optional.get().isActive())
                super.invalidate("Registro no disponible. ");
        } else super.invalidate("Registro no encontrado. ");
        return this;
    }

}