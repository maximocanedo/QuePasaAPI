package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OwnerServiceImpl implements frgp.utn.edu.ar.quepasa.service.OwnerService {

    private final AuthenticationService authenticationService;

    @Autowired
    public OwnerServiceImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public OwnerValidatorBuilder of(Ownable object) {
        User user = authenticationService.getCurrentUserOrDie();
        return OwnerValidatorBuilder.create(object, user);
    }

}
