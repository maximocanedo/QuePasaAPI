package frgp.utn.edu.ar.quepasa.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
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
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserPatchEditRequest user) {
        return ResponseEntity.ok(userService.update(username, user));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/me/mail")
    public ResponseEntity<Mail> saveMail(@RequestBody String sub) throws AuthenticationCredentialsNotFoundException, MessagingException {
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

    @PostMapping("/me/phone")
    public ResponseEntity<Phone> savePhone(@RequestBody String content) throws AuthenticationCredentialsNotFoundException, AuthenticationFailedException {
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
    public ResponseEntity<User> getMe() {
        User me = authenticationService.getCurrentUserOrDie();
        return new ResponseEntity<>(me, HttpStatus.OK);
    }

    @PostMapping("/me/totp")
    public ResponseEntity<byte[]> enableTotpAuthentication() {
        byte[] qr = authenticationService.createTotpSecret();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qr, headers, HttpStatus.OK);
    }

    @DeleteMapping("/me/totp")
    public ResponseEntity<?> disableTotpAuthentication() {
        authenticationService.disableTotp();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody UserPatchEditRequest user) {
        return ResponseEntity.ok(userService.update(user));
    }

    @PostMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody String newPassword) {
        userService.updatePassword(newPassword);
        return ResponseEntity.ok("Password reset. ");
    }

    @GetMapping()
    public ResponseEntity<Page<User>> getAll(String q, Pageable pageable) {
        return ResponseEntity.ok(userService.listUser(q, pageable));
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

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ResponseError> handleAuthError(AuthenticationCredentialsNotFoundException e) {
        return new ResponseEntity<>(new ResponseError(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ResponseError> handleMessagingError(MessagingException e) {
        return new ResponseEntity<>(new ResponseError("Hubo un error al intentar enviar un correo electrónico. "), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<ResponseError> handleFail(Fail e) {
        return ResponseEntity.status(e.getStatus()).body(new ResponseError(e.getMessage()));
    }
}
