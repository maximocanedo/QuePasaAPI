package frgp.utn.edu.ar.quepasa.service.impl;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements frgp.utn.edu.ar.quepasa.service.MailSenderService {

    private final Session mailSession;

    @Autowired
    public MailSenderServiceImpl(Session mailSession) {
        this.mailSession = mailSession;
    }

    @Override
    public void send(String to, String subject, String body) throws AddressException, MessagingException {
        Message message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress("verificadorgrupotusi@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
    }

    @Override
    public String otp(int code) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tu código de verificación es: ");
        sb.append(code);
        return sb.toString();
    }

    @Override
    public String recoverPasswordBody(String code) {
        StringBuilder sb = new StringBuilder("Solicitaste la recuperación de tu cuenta de QuePasa. \n");
        sb.append("Si fuiste vos, usá este código para autorizar el cambio de contraseña: ");
        sb.append(code);
        sb.append("\n\n");
        sb.append("Si no solicitaste esto, ignorá este correo. \n\nSaludos.");
        return sb.toString();
    }

}
