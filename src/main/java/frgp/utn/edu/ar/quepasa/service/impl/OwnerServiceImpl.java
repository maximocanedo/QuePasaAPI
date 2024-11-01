package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidator;
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
    public OwnerValidator of(Ownable object) {
        User user = authenticationService.getCurrentUserOrDie();
        return OwnerValidator.create(object, user);
    }

}
