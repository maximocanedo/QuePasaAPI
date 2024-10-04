package frgp.utn.edu.ar.quepasa.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public interface MailSenderService {
    void send(String to, String subject, String body) throws AddressException, MessagingException;

    String otp(int code);

    String recoverPasswordBody(String code);
}
