package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequestAction;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.PhoneRepository;
import frgp.utn.edu.ar.quepasa.repository.SingleUseRequestRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.JwtService;
import frgp.utn.edu.ar.quepasa.service.MailSenderService;
import frgp.utn.edu.ar.quepasa.service.SingleUseRequestService;
import frgp.utn.edu.ar.quepasa.service.validators.users.PasswordValidator;
import jakarta.mail.MessagingException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Optional;

@Service("singleUseRequestService")
public class SingleUseRequestServiceImpl implements SingleUseRequestService {

    private final SingleUseRequestRepository singleUseRequestRepository;
    private final MailSenderService mailSenderService;
    private final MailRepository mailRepository;
    private final PhoneRepository phoneRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public SingleUseRequestServiceImpl(
            SingleUseRequestRepository singleUseRequestRepository,
            MailSenderService mailSenderService,
            MailRepository mailRepository,
            PhoneRepository phoneRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.singleUseRequestRepository = singleUseRequestRepository;
        this.mailSenderService = mailSenderService;
        this.mailRepository = mailRepository;
        this.phoneRepository = phoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String generateHexOTP() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[3];
        random.nextBytes(bytes);
        StringBuilder code = new StringBuilder();
        for (byte b : bytes) {
            code.append(String.format("%02x", b));
        }
        return (code.toString());
    }

    /**
     * <b>Crea una solicitud de cambio de contraseña. </b>
     * <p>Para usuarios que hayan olvidado sus credenciales. </p>
     */
    @Override
    public SingleUseRequest passwordResetRequest(PasswordResetRequest request) {
        Optional<User> u;
        String via = "";
        if(request.getEmail() != null && !request.getEmail().isBlank()) {
            Optional<Mail> m = mailRepository.findByMail(request.getEmail(), request.getUsername());
            if(m.isEmpty()) throw new Fail("Mail is not linked to any account. ", HttpStatus.BAD_REQUEST);
            Mail mail = m.get();
            Hibernate.initialize(mail.getUser());
            via = "mail";
            u = Optional.of(mail.getUser());
        } else if(request.getPhone() != null && !request.getPhone().isBlank()) {
            Optional<Phone> p = phoneRepository.findByPhone(request.getPhone(), request.getUsername());
            if(p.isEmpty()) throw new Fail("Phone is not linked to any account. ", HttpStatus.BAD_REQUEST);
            Phone phone = p.get();
            Hibernate.initialize(phone.getUser());
            via = "sms";
            u = Optional.of(phone.getUser());
        } else throw new Fail("Must provide an email or a phone number. ", HttpStatus.BAD_REQUEST);
        User user = u.get();
        if(!user.isActive()) {
            throw new Fail("The account you are trying to reset password is not active. ", HttpStatus.UNAUTHORIZED);
        }
        String otp = generateHexOTP();
        if(via.equals("mail")) {
            try {
                mailSenderService.send(request.getEmail(), "Recuperá tu cuenta", mailSenderService.recoverPasswordBody(otp));
            } catch(MessagingException e) {
                throw new Fail("Error while trying to send the code. Operation was aborted. ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            otp = "111111"; // No hay plata para APIs de SMS o WhatsApp.
        }
        var document = new SingleUseRequest();
        document.setUser(u.get());
        document.setAction(SingleUseRequestAction.RESET_PASSWORD);
        document.setActive(true);
        document.setHash(passwordEncoder.encode(otp));
        document.setRequested(new Timestamp(System.currentTimeMillis()));
        document = singleUseRequestRepository.saveAndFlush(document);
        return document;
    }

    /**
     * <b>Intenta cambiar la contraseña. </b>
     * <p>Si el código es correcto, cambia la contraseña e inicia sesión del usuario. </p>
     */
    @Override
    public JwtAuthenticationResponse passwordResetAttempt(PasswordResetAttempt request) {
        var opt = singleUseRequestRepository.findById(request.getId());
        if(opt.isEmpty()) throw new Fail("Request with id " + request.getId() + " was not found. ", HttpStatus.NOT_FOUND);
        var document = opt.get();
        if(document.isExpired())
            throw new Fail("Request has expired. ", HttpStatus.UNAUTHORIZED);
        if(document.getAction() != SingleUseRequestAction.RESET_PASSWORD)
            throw new Fail("Cannot use " + document.getAction().name() + " request to recover a password. ", HttpStatus.BAD_REQUEST);
        if(!passwordEncoder.matches(request.getCode(), document.getHash()))
            throw new Fail("Wrong code. ", HttpStatus.UNAUTHORIZED);
        var user = userRepository
                .findByUsername(document.getUser().getUsername())
                .orElseThrow(() -> new Fail("Username not found. "));
        var password = new PasswordValidator(request.getNewPassword())
                .lengthIsEightCharactersOrMore()
                .hasOneUpperCaseLetter()
                .hasOneLowerCaseLetter()
                .hasOneDigit()
                .hasOneSpecialCharacter()
                .build();
        document.setActive(false);
        user.setPassword(passwordEncoder.encode(password));
        singleUseRequestRepository.saveAndFlush(document);
        userRepository.save(user);
        // El usuario se autenticó, por lo que no es necesario emitir un token JWT parcial.
        var jwt = jwtService.generateToken(user, false);
        JwtAuthenticationResponse e = new JwtAuthenticationResponse();
        e.setToken(jwt);
        e.setTotpRequired(false);
        return e;
    }
}
