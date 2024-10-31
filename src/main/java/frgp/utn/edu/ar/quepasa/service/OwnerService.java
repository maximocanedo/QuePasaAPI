package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidator;

public interface OwnerService {
    OwnerValidator of(Ownable object);
}
