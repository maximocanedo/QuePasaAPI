package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.service.validators.commons.OwnerValidator;

public interface OwnerService {
    OwnerValidator of(Ownable object);
}
