package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.ValidatorBuilder;

import java.util.Optional;

public class NeighbourhoodValidator extends ValidatorBuilder<NeighbourhoodValidator, Neighbourhood> {

    public NeighbourhoodValidator(Neighbourhood value) {
        super(value, "neighbourhood");
    }

    public NeighbourhoodValidator isActive(NeighbourhoodRepository repository) {
        Optional<Neighbourhood> optional = repository.findById(getValue().getId());
        if(optional.isPresent()) {
            if(!optional.get().isActive())
                super.invalidate("Registro no disponible. ");
        } else super.invalidate("Registro no encontrado. ");
        return this;
    }

}
