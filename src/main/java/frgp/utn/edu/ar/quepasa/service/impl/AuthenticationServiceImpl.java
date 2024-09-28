package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.PhoneRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.JwtService;
import frgp.utn.edu.ar.quepasa.service.MailSenderService;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private MailSenderService mailSenderServiceImpl;
    @Autowired private MailRepository mailRepository;
    @Autowired private PhoneRepository phoneRepository;

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) { return Optional.empty(); }
        if(authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails)authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }
    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        JwtAuthenticationResponse e = new JwtAuthenticationResponse();
        e.setToken(jwt);
        return e;
    }

    @Override
    public JwtAuthenticationResponse login(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        System.out.println(user);
        var jwt = jwtService.generateToken(user);
        JwtAuthenticationResponse e = new JwtAuthenticationResponse();
        e.setToken(jwt);
        return e;
    }

    @Override
    public int generateOTP() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    @Override
    public String generateVerificationCodeHash(int code) {
        return passwordEncoder.encode(String.valueOf(code));
    }

    @Override
    public Mail requestMailVerificationCode(@NotNull VerificationRequest request) throws MessagingException {
        User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);
        int code = generateOTP();
        String hash = generateVerificationCodeHash(code);
        Mail mail = new Mail();
        mail.setMail(request.getSubject());
        mail.setUser(me);
        mail.setHash(hash);
        mail.setRequestedAt(new Timestamp(System.currentTimeMillis()));
        mail.setVerified(false);
        mailRepository.save(mail);
        try {
            mailSenderServiceImpl.send(request.getSubject(), mailSenderServiceImpl.otp(code), mailSenderServiceImpl.otp(code));
        } catch(MessagingException e) {
            e.printStackTrace();
            throw e;
        }
        return mail;
    }

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

    @Override
    public Phone requestSMSVerificationCode(@NotNull VerificationRequest request) throws AuthenticationFailedException {
       User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);
       int code = 111111;
       String hash = generateVerificationCodeHash(code);
       Phone phone = new Phone();
       phone.setPhone(request.getSubject());
       phone.setUser(me);
       phone.setHash(hash);
       phone.setRequestedAt(new Timestamp(System.currentTimeMillis()));
       phone.setVerified(false);
       phoneRepository.save(phone);
       return phone;
    }

    @Override
    public Phone verifyPhone(CodeVerificationRequest request) throws AuthenticationFailedException {
        User me = getCurrentUser().orElseThrow(AuthenticationFailedException::new);
        Phone phone = phoneRepository
                .findByPhoneAndUser(request.getSubject(), me)
                .orElseThrow(NoSuchElementException::new);
        if(phone.isVerified()) return phone;
        if(passwordEncoder.matches(request.getCode(), phone.getHash())) {
            phone.setVerified(true);
            phone.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
            phoneRepository.save(phone);
            return phone;
        }
        throw new AuthenticationFailedException("Code not valid. ");
    }

}