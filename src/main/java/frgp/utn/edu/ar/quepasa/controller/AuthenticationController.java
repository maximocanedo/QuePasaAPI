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

    /**
     * Registra a un usuario en la aplicacion
     *
     * @param request informacion del usuario a registrar
     * @return un JWT con la informacipn del usuario
     */
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    /**
     * Inicia sesi n en la aplicaci n con credenciales
     *
     * @param request informaci n del usuario a loguear
     * @return un JWT con la informaci n del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

/**
 * Inicia sesión en la aplicación utilizando un código TOTP.
 *
 * @param totp el código TOTP proporcionado por el usuario
 * @return una respuesta que contiene un JWT con la información del usuario
 */
    @PostMapping("/login/totp")
    public ResponseEntity<JwtAuthenticationResponse> loginWithTotp(@RequestBody String totp) {
        return ResponseEntity.ok(authenticationService.loginWithTotp(totp));
    }

/**
 * Solicita un cambio de contraseña para un usuario que ha olvidado sus credenciales.
 *
 * @param request contiene la información necesaria para identificar al usuario, 
 *                 como el correo electrónico o número de teléfono asociado a la cuenta.
 * @return una respuesta que contiene una solicitud de uso único para el reinicio de contraseña.
 */
    @PostMapping("/recover")
    public ResponseEntity<SingleUseRequest> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(singleUseRequestService.passwordResetRequest(request));
    }

    /**
     * Intenta cambiar la contraseña de un usuario que ha olvidado sus credenciales.
     *
     * @param attempt contiene el código OTP proporcionado por el usuario y su nueva contraseña.
     * @return una respuesta que contiene un JWT con la información del usuario,
     *         o un error HTTP 400 si el código OTP es incorrecto,
     *         o un error HTTP 401 si la cuenta del usuario no existe o no es activa.
     */
    @PostMapping("/recover/reset")
    public ResponseEntity<JwtAuthenticationResponse> resetPassword(@RequestBody PasswordResetAttempt attempt) {
        return ResponseEntity.ok(singleUseRequestService.passwordResetAttempt(attempt));
    }

}

