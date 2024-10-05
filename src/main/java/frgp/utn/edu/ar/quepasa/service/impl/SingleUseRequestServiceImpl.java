package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequestAction;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.PhoneRepository;
import frgp.utn.edu.ar.quepasa.repository.SingleUseRequestRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.JwtService;
import frgp.utn.edu.ar.quepasa.service.SingleUseRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class SingleUseRequestServiceImpl implements SingleUseRequestService {

    @Autowired private SingleUseRequestRepository singleUseRequestRepository;
    @Autowired private MailRepository mailRepository;
    @Autowired private PhoneRepository phoneRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private JwtService jwtService;

    public String generateHexOTP() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[3];
        random.nextBytes(bytes);
        StringBuilder code = new StringBuilder();
        for (byte b : bytes) {
            code.append(String.format("%02x", b));
        }
        return passwordEncoder.encode(code.toString());
    }

    /**
     * <b>Crea una solicitud de cambio de contraseña. </b>
     * <p>Para usuarios que hayan olvidado sus credenciales. </p>
     */
    @Override
    public SingleUseRequest passwordResetRequest(PasswordResetRequest request) {
        Optional<User> u;
        if(request.getEmail() != null && !request.getEmail().isBlank()) {
            Optional<Mail> m = mailRepository.findByMail(request.getEmail(), request.getUsername());
            if(m.isEmpty()) throw new Fail("Mail is not linked to any account. ", HttpStatus.BAD_REQUEST);
            Mail mail = m.get();
            u = Optional.of(mail.getUser());
        } else if(request.getPhone() != null && !request.getPhone().isBlank()) {
            Optional<Phone> p = phoneRepository.findByPhone(request.getPhone(), request.getUsername());
            if(p.isEmpty()) throw new Fail("Phone is not linked to any account. ", HttpStatus.BAD_REQUEST);
            Phone phone = p.get();
            u = Optional.of(phone.getUser());
        } else throw new Fail("Must provide an email or a phone number. ", HttpStatus.BAD_REQUEST);
        User user = u.get();
        if(!user.isAccountNonExpired() || !user.isAccountNonLocked() || !user.isCredentialsNonExpired() || !user.isEnabled() || !user.isActive()) {
            throw new Fail("The account you are trying to reset password is not active. ", HttpStatus.UNAUTHORIZED);
        }
        var document = new SingleUseRequest();
        document.setUser(u.get());
        document.setAction(SingleUseRequestAction.RESET_PASSWORD);
        document.setActive(true);
        document.setHash(generateHexOTP());
        document = singleUseRequestRepository.save(document);
        return document;

    }

    /**
     * <b>Intenta cambiar la contraseña. </b>
     * <p>Si el código es correcto, cambia la contraseña e inicia sesión del usuario. </p>
     */
    @Override
    public JwtAuthenticationResponse passwordResetAttempt(PasswordResetAttempt request) {
        Optional<SingleUseRequest> opt = singleUseRequestRepository.findById(request.getId());
        // TODO Comprobar que no esté expirado.
        if(opt.isEmpty()) throw new Fail("Request with id " + request.getId() + " was not found. ", HttpStatus.NOT_FOUND);
        var document = opt.get();
        if(document.getAction() != SingleUseRequestAction.RESET_PASSWORD)
            throw new Fail("Cannot use " + document.getAction().name() + " request to recover a password. ", HttpStatus.BAD_REQUEST);
        if(!passwordEncoder.matches(request.getCode(), document.getHash()))
            throw new Fail("Wrong code. ", HttpStatus.UNAUTHORIZED);
        document.setActive(false);
        User user = document.getUser();
        authenticationService.validatePassword(request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        singleUseRequestRepository.save(document);
        // El usuario se autenticó, por lo que no es necesario emitir un token JWT parcial.
        var jwt = jwtService.generateToken(user, false);
        JwtAuthenticationResponse e = new JwtAuthenticationResponse();
        e.setToken(jwt);
        e.setTotpRequired(user.hasTotpEnabled());
        return e;
    }
}
