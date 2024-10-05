package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import org.springframework.stereotype.Service;

public interface SingleUseRequestService {

    SingleUseRequest passwordResetRequest(PasswordResetRequest request);
    JwtAuthenticationResponse passwordResetAttempt(PasswordResetAttempt request);

}
