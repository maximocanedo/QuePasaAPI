package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;

import java.security.SecureRandom;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
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
    public String generateVerificationCodeHash() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return passwordEncoder.encode(String.valueOf(code));
    }

    @Override
    public boolean compareCode(String verificationCodeHash, String code) {
        return false;
    }

    @Override
    public Mail requestMailVerificationCode(VerificationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) return null;

        Mail mail = new Mail();
        mail.setMail(request.getSubject());
        return null;
    }

    @Override
    public Mail verifyMail(CodeVerificationRequest request) {
        return null;
    }

    @Override
    public Phone requestSMSVerificationCode(VerificationRequest request) {
        return null;
    }

    @Override
    public Phone verifyPhone(CodeVerificationRequest request) {
        return null;
    }

}