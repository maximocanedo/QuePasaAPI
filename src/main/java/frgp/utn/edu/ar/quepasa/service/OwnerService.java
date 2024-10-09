package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidatorBuilder;

public interface OwnerService {
    OwnerValidatorBuilder of(Ownable object);
}
