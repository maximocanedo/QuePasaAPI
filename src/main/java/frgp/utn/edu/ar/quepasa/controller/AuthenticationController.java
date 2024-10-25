package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.SingleUseRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final SingleUseRequestService singleUseRequestService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, SingleUseRequestService singleUseRequestService) {
        this.authenticationService = authenticationService;
        this.singleUseRequestService = singleUseRequestService;
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/login/totp")
    public ResponseEntity<JwtAuthenticationResponse> loginWithTotp(@RequestBody String totp) {
        return ResponseEntity.ok(authenticationService.loginWithTotp(totp));
    }

    @PostMapping("/recover")
    public ResponseEntity<SingleUseRequest> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(singleUseRequestService.passwordResetRequest(request));
    }

    @PostMapping("/recover/reset")
    public ResponseEntity<JwtAuthenticationResponse> resetPassword(@RequestBody PasswordResetAttempt attempt) {
        return ResponseEntity.ok(singleUseRequestService.passwordResetAttempt(attempt));
    }

}

