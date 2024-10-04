package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
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
        System.out.println("TOTPCODE: '" + totp + "'.");
        return ResponseEntity.ok(authenticationService.loginWithTotp(totp));
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<ResponseError> handleFail(Fail e) {
        return ResponseEntity.status(e.getStatus()).body(new ResponseError(e.getMessage()));
    }
}

