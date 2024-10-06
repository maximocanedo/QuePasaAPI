package frgp.utn.edu.ar.quepasa.service;


import de.taimos.totp.TOTPData;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;

import java.util.Optional;

public interface AuthenticationService {
    Optional<User> getCurrentUser();

    User getCurrentUserOrDie();

    String validatePassword(String password);

    JwtAuthenticationResponse signup(SignUpRequest request);
    JwtAuthenticationResponse login(SigninRequest request);


    TOTPData generateSecret(String username);

    byte[] createTotpSecret();

    void disableTotp();

    JwtAuthenticationResponse loginWithTotp(String code);

    int generateOTP();

    String generateVerificationCodeHash(int code);

    Mail requestMailVerificationCode(VerificationRequest request) throws MessagingException;
    Mail verifyMail(CodeVerificationRequest request) throws AuthenticationFailedException;

    Phone requestSMSVerificationCode(VerificationRequest request) throws AuthenticationFailedException;
    Phone verifyPhone(CodeVerificationRequest request) throws AuthenticationFailedException;

}
