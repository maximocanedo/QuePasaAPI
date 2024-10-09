package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OwnerServiceImpl implements frgp.utn.edu.ar.quepasa.service.OwnerService {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public OwnerValidatorBuilder of(Ownable object) {
        return new OwnerValidatorBuilder(object, authenticationService);
    }

}
