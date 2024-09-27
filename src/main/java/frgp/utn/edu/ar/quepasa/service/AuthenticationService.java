package frgp.utn.edu.ar.quepasa.service;


import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);
    JwtAuthenticationResponse login(SigninRequest request);

    String generateVerificationCodeHash();
    boolean compareCode(String verificationCodeHash, String code);

    Mail requestMailVerificationCode(VerificationRequest request);
    Mail verifyMail(CodeVerificationRequest request);

    Phone requestSMSVerificationCode(VerificationRequest request);
    Phone verifyPhone(CodeVerificationRequest request);

}
