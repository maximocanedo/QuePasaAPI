package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.response.TotpEnablingResponse;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import quepasa.api.exceptions.ValidationError;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.HEAD)
    public ResponseEntity<?> existsByUsername(@PathVariable String username) {
        var x = userService.existsByUsername(username);
        return ResponseEntity.status(x ? HttpStatus.OK : HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PatchMapping("/{username}")
    public ResponseEntity<?> update(@PathVariable String username, @RequestBody UserPatchEditRequest request) {
        return ResponseEntity.ok(userService.update(username, request));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> disable(@PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/me/mail")
    public ResponseEntity<Mail> requestMailVerificationCode(@RequestBody String sub) throws AuthenticationCredentialsNotFoundException, MessagingException {
        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setSubject(sub);
        Mail mail = authenticationService.requestMailVerificationCode(verificationRequest);
        return new ResponseEntity<>(mail, HttpStatus.OK);
    }

    @PostMapping("/me/mail/verify")
    public ResponseEntity<Mail> verifyMail(@RequestBody CodeVerificationRequest req) throws AuthenticationCredentialsNotFoundException, AuthenticationFailedException {
        Mail mail = authenticationService.verifyMail(req);
        return new ResponseEntity<>(mail, HttpStatus.OK);
    }

    @DeleteMapping("/me/mail")
    public ResponseEntity<?> deleteMail(@RequestParam String subject) {
        authenticationService.deleteMail(subject);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/me/phone")
    public ResponseEntity<?> deletePhone(@RequestParam String subject) {
        authenticationService.deletePhone(subject);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/me/phone")
    public ResponseEntity<Phone> requestPhoneVerificationCode(@RequestBody String content) throws AuthenticationCredentialsNotFoundException, AuthenticationFailedException {
        VerificationRequest req = new VerificationRequest();
        req.setSubject(content);
        Phone phone = authenticationService.requestSMSVerificationCode(req);
        return new ResponseEntity<>(phone, HttpStatus.OK);
    }

    @PostMapping("/me/phone/verify")
    public ResponseEntity<Phone> verifyPhone(@RequestBody CodeVerificationRequest req) throws AuthenticationCredentialsNotFoundException, AuthenticationFailedException {
        Phone phone = authenticationService.verifyPhone(req);
        return new ResponseEntity<>(phone, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<User> me() {
        User me = authenticationService.getCurrentUserOrDie();
        return new ResponseEntity<>(me, HttpStatus.OK);
    }

    @PostMapping("/me/totp")
    public ResponseEntity<TotpEnablingResponse> enableTotpAuthentication() {
        var totp = authenticationService.enableTotp();
        return ResponseEntity.ok(totp);
    }

    @DeleteMapping("/me/totp")
    public ResponseEntity<?> disableTotpAuthentication() {
        authenticationService.disableTotp();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateMe(@RequestBody UserPatchEditRequest request) {
        User updated = userService.update(request);
        return ResponseEntity.ok().body(updated);
    }

    @PostMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody String newPassword) {
        userService.updatePassword(newPassword);
        return ResponseEntity.ok("Password reset. ");
    }

    @GetMapping
    public ResponseEntity<Page<User>> search(String q, Pageable pageable) {
        return ResponseEntity.ok(userService.search(q, pageable));
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

}
