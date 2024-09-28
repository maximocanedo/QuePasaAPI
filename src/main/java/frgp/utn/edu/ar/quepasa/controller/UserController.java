package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserById(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PatchMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody User user) {
        return ResponseEntity.ok(userService.update(username, user));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/me/mail")
    public ResponseEntity<Mail> saveMail(@RequestBody VerificationRequest req) throws MessagingException {
        Mail mail = authenticationService.requestMailVerificationCode(req);
        return new ResponseEntity<>(mail, HttpStatus.OK);
    }

    @PostMapping("/me/mail/verify")
    public ResponseEntity<Mail> verifyMail(@RequestBody CodeVerificationRequest req) throws AuthenticationFailedException {
        Mail mail = authenticationService.verifyMail(req);
        return new ResponseEntity<>(mail, HttpStatus.OK);
    }

    @PostMapping("/me/phone")
    public ResponseEntity<Phone> savePhone(@RequestBody VerificationRequest req) throws AuthenticationFailedException {
        Phone phone = authenticationService.requestSMSVerificationCode(req);
        return new ResponseEntity<>(phone, HttpStatus.OK);
    }

    @PostMapping("/me/phone/verify")
    public ResponseEntity<Phone> verifyPhone(@RequestBody CodeVerificationRequest req) throws AuthenticationFailedException {
        Phone phone = authenticationService.verifyPhone(req);
        return new ResponseEntity<>(phone, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe() throws AuthenticationFailedException {
        User me = authenticationService.getCurrentUserOrDie();
        return new ResponseEntity<>(me, HttpStatus.OK);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                User currentUser = userService.findByUsername(((UserDetails) principal).getUsername());
                return ResponseEntity.ok(userService.update(currentUser.getUsername(), user));
            }
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                User currentUser = userService.findByUsername(((UserDetails) principal).getUsername());
                userService.delete(currentUser.getUsername());
                return ResponseEntity.ok(HttpStatus.NO_CONTENT);
            }
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }

    /// EXCEPCIONES

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ResponseError> handleAuthError(AuthenticationFailedException e) {
        return new ResponseEntity<>(new ResponseError(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ResponseError> handleMessagingError(MessagingException e) {
        return new ResponseEntity<>(new ResponseError("Hubo un error al intentar enviar un correo electrónico. "), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
