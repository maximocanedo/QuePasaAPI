package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.springframework.stereotype.Service;


@Service
public class OwnerServiceImpl implements frgp.utn.edu.ar.quepasa.service.OwnerService {

    private final AuthenticationService authenticationService;

    public OwnerServiceImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public OwnerValidatorBuilder of(Ownable object) {
        return new OwnerValidatorBuilder(object, authenticationService);
    }

}
