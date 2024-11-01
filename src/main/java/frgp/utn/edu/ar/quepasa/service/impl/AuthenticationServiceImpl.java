package frgp.utn.edu.ar.quepasa.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.taimos.totp.TOTP;
import de.taimos.totp.TOTPData;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.PhoneRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.JwtService;
import frgp.utn.edu.ar.quepasa.service.MailSenderService;
import quepasa.api.validators.users.*;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailSenderService mailSenderService;
    private final MailRepository mailRepository;
    private final PhoneRepository phoneRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;

    @Autowired
    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            MailSenderService mailSenderService,
            MailRepository mailRepository,
            PhoneRepository phoneRepository,
            NeighbourhoodRepository neighbourhoodRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailSenderService = mailSenderService;
        this.mailRepository = mailRepository;
        this.phoneRepository = phoneRepository;
        this.neighbourhoodRepository = neighbourhoodRepository;
    }

    /**
     * <b>Devuelve el usuario autenticado</b>
     */
    @Override
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) { return Optional.empty(); }
        if(authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails)authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }

    /**
     * <b>Devuelve el usuario autenticado, o lanza una excepción. </b>
     */
    @Override
    public @NotNull User getCurrentUserOrDie() throws AuthenticationCredentialsNotFoundException {
        return getCurrentUser()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No user authenticated. "));
    }

    @Override
    public String validatePassword(String password) {
        return new PasswordValidator(password)
                .lengthIsEightCharactersOrMore()
                .hasOneUpperCaseLetter()
                .hasOneLowerCaseLetter()
                .hasOneDigit()
                .hasOneSpecialCharacter()
                .build();
    }

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = new User();
        if(request.getNeighbourhoodId() != null) {
            Neighbourhood n = neighbourhoodRepository
                    .findActiveNeighbourhoodById(request.getNeighbourhoodId())
                    .orElseThrow(() -> new Fail("Neighbourhood not found. ", HttpStatus.BAD_REQUEST));
            user.setNeighbourhood(n);
        }
        var name = new NameValidator(request.getName())
                .validateCompoundNames()
                .build();
        var username = new UsernameValidator(request.getUsername())
                .meetsMinimumLength()
                .meetsMaximumLength()
                .doesntHaveIllegalCharacters()
                .doesntHaveTwoDotsOrUnderscoresInARow()
                .neitherStartsNorEndsWithDoubleDotsOrUnderscores()
                .isAvailable(userRepository)
                .build();
        var password = new PasswordValidator(request.getPassword())
                .lengthIsEightCharactersOrMore()
                .hasOneUpperCaseLetter()
                .hasOneLowerCaseLetter()
                .hasOneDigit()
                .hasOneSpecialCharacter()
                .build();

        user.setName(name);
        user.setUsername(username);
        user.setAddress("");
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);

        userRepository.save(user);

        var jwt = jwtService.generateToken(user, false);
        JwtAuthenticationResponse e = new JwtAuthenticationResponse();
        e.setToken(jwt);
        e.setTotpRequired(false);

        return e;
    }

    @Override
    public JwtAuthenticationResponse login(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new Fail("Invalid username or password.", HttpStatus.BAD_REQUEST));
        var jwt = jwtService.generateToken(user, user.hasTotpEnabled());
        JwtAuthenticationResponse e = new JwtAuthenticationResponse();
        e.setToken(jwt);
        e.setTotpRequired(user.hasTotpEnabled());
        return e;
    }

    @Override
    public TOTPData generateSecret(String username) {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return new TOTPData("QuePasa", username, bytes);
    }

    @Override
    public byte[] createTotpSecret() {
        User user = getCurrentUserOrDie();
        if(user.hasTotpEnabled()) throw new Fail("Totp already enabled. ", HttpStatus.CONFLICT);
        TOTPData data = generateSecret(user.getUsername());
        try {
            byte[] qr = generateQRCodeImage(data.getUrl());
            user.setTotp(data.getSecretAsHex());
            userRepository.save(user);
            return qr;
        } catch(WriterException | IOException e) {
            throw new Fail("Error trying to generate QR Code. Operation was aborted.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] generateQRCodeImage(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250, hintMap);

        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    @Override
    public void disableTotp() {
        User user = getCurrentUserOrDie();
        if(!user.hasTotpEnabled()) throw new Fail("Totp already disabled. ", HttpStatus.OK);
        user.setTotp("no-totp");
        userRepository.save(user);
    }

    public boolean validateTOTP(String secret, String code) {
        return TOTP.validate(secret, code);
    }

    @Override
    public JwtAuthenticationResponse loginWithTotp(String code) {
        User user = getCurrentUserOrDie();
        if(!user.hasTotpEnabled()) throw new Fail("Totp not enabled. ", HttpStatus.CONFLICT);
        if(validateTOTP(user.getTotp(), code)) {
            var jwt = jwtService.generateToken(user, user.hasTotpEnabled());
            JwtAuthenticationResponse e = new JwtAuthenticationResponse();
            e.setToken(jwt);
            e.setTotpRequired(false);
            return e;
        }
        throw new Fail("Invalid TOTP code. ", HttpStatus.UNAUTHORIZED);
    }

    /**
     * <p>Genera un código OTP de seis dígitos. </p>
     */
    @Override
    public int generateOTP() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    /**
     * <p>Genera un hash a partir del código dado. </p>
     */
    @Override
    public String generateVerificationCodeHash(int code) {
        return passwordEncoder.encode(String.valueOf(code));
    }


    /**
     * <b>Registra un correo electrónico</b>
     * <p>Y envía un correo electrónico con un código OTP de seis dígitos para su posterior verificación. </p>
     */
    @Override
    public Mail requestMailVerificationCode(@NotNull VerificationRequest request) throws MessagingException {
        User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);

        var subject = new MailValidator(request.getSubject())
                .isValidAddress()
                .build();

        int code = generateOTP();
        String hash = generateVerificationCodeHash(code);
        Mail mail = new Mail();
        mail.setMail(subject);
        mail.setUser(me);
        mail.setHash(hash);
        mail.setRequestedAt(new Timestamp(System.currentTimeMillis()));
        mail.setVerified(false);
        mailRepository.save(mail);

        try {
            mailSenderService.send(request.getSubject(), mailSenderService.otp(code), mailSenderService.otp(code));
        } catch(MessagingException e) {
            e.printStackTrace();
            throw e;
        }
        return mail;
    }

    /**
     * <b>Verifica un correo electrónico</b>
     * <p>Intenta verificar que el código OTP recibido coincida con el almacenado para marcar como verificada la dirección de correo. </p>
     */
    @Override
    public Mail verifyMail(CodeVerificationRequest request) throws AuthenticationFailedException {
        User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);
        Mail mail = mailRepository
                .findByMailAndUser(request.getSubject(), me)
                .orElseThrow(NoSuchElementException::new);
        if(mail.isVerified()) return mail;
        if(passwordEncoder.matches(request.getCode(), mail.getHash())) {
            mail.setVerified(true);
            mail.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
            mailRepository.save(mail);
            return mail;
        }
        throw new AuthenticationFailedException("Code not valid. ");
    }

    /**
     * <b>Registra un número de teléfono</b>
     * <p>Debería enviar un SMS con un código OTP de seis dígitos para su posterior verificación. </p>
     * <p><b>Aviso importante: </b>Dada la falta de presupuesto para contratar una API de envío de SMS, no se implementará esta funcionalidad, y se podrá verificar cualquier registro con el código `111 - 111`.</p>
     */
    @Override
    public Phone requestSMSVerificationCode(@NotNull VerificationRequest request) throws AuthenticationFailedException {
       User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);
       int code = 111111;
       String hash = generateVerificationCodeHash(code);
       String phoneNumber = new PhoneValidator(request.getSubject())
               .isValidPhoneNumber()
               .format().build();
       Phone phone = new Phone();
       phone.setPhone(phoneNumber);
       phone.setUser(me);
       phone.setHash(hash);
       phone.setRequestedAt(new Timestamp(System.currentTimeMillis()));
       phone.setVerified(false);
       phoneRepository.save(phone);

       return phone;
    }

    /**
     * <b>Verifica un número de teléfono</b>
     * <p>Debería intentar verificar que el código OTP recibido coincida con el almacenado para marcar como verificado el número de teléfono. </p>
     * <p><b>Aviso importante: </b>Dada la falta de presupuesto para contratar una API de envío de SMS, no se implementará esta funcionalidad, y se podrá verificar cualquier registro con el código `111 - 111`.</p>
     */
    @Override
    public Phone verifyPhone(CodeVerificationRequest request) throws AuthenticationFailedException {
        User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);
        var number = new PhoneValidator(request.getSubject()).format().build();
        Phone phone = phoneRepository
                .findByPhoneAndUser(number, me)
                .orElseThrow(() -> new Fail("Phone not found. ", HttpStatus.BAD_REQUEST));
        if(phone.isVerified()) return phone;
        if(passwordEncoder.matches(request.getCode(), phone.getHash())) {
            phone.setVerified(true);
            phone.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
            phoneRepository.save(phone);
            return phone;
        }
        throw new Fail("Code not valid. ", HttpStatus.BAD_REQUEST);
    }

}