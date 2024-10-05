package frgp.utn.edu.ar.quepasa.service;


import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.PhoneRepository;
import frgp.utn.edu.ar.quepasa.repository.SingleUseRequestRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SingleUseRequestServiceTests {

    @Autowired private SingleUseRequestService singleUseRequestService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private NeighbourhoodRepository neighbourhoodRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MailRepository mailRepository;
    @Autowired private PhoneRepository phoneRepository;

    private final String testUsername = "surs.test.0001";
    private final String testMail = "42684627.canedo@gmail.com";
    private final String testPhone = "+541130388784";
    private final String testPassword = "fUn1cV1i?Fvnl$u14€";
    private final String testNewPassword = "P4r14%P1ú%p14n0%3%n355vn0%53nt1r4";
    private UUID requestId;
    @Autowired
    private SingleUseRequestRepository singleUseRequestRepository;

    public User getTestUser() {
        Optional<User> opt = userRepository.findByUsername(testUsername);
        if(opt.isEmpty()) throw new Fail("User test was nismaned. ");
        return opt.get();
    }

    @BeforeAll
    @Transactional
    public void setUp() {
        try {
            userRepository.delete(getTestUser());
            userRepository.deleteById(getTestUser().getId());
            userRepository.deleteByUsername(testUsername);
        } catch(Exception expected) {}
        finally {
            if(userRepository.findByUsername(testUsername).isEmpty()) {
                Neighbourhood x = neighbourhoodRepository.findAll().getFirst();
                User user = new User();
                user.setUsername(testUsername);
                user.setPassword(passwordEncoder.encode(testPassword));
                user.setActive(true);
                user.setName("Usuario de prueba para solicitudes de único uso");
                user.setAddress("");
                user.setNeighbourhood(x);
                user = userRepository.save(user);
                var mail = new Mail();
                mail.setMail(testMail);
                mail.setVerified(true);
                mail.setUser(user);
                mail.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
                mail.setRequestedAt(new Timestamp(System.currentTimeMillis()));
                mail.setHash(passwordEncoder.encode("111111"));
                mail = mailRepository.save(mail);
                var phone = new Phone();
                phone.setPhone(testPhone);
                phone.setVerified(true);
                phone.setUser(user);
                phone.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
                phone.setRequestedAt(new Timestamp(System.currentTimeMillis()));
                phone.setHash(passwordEncoder.encode("111111"));
                phone = phoneRepository.save(phone);
            }

        }
    }

    @AfterAll
    @Transactional
    public void endTests() {
        userRepository.delete(getTestUser());
    }


    @Test
    @Order(1)
    @DisplayName("Solicitar cambio de contraseña: Correo + datos válidos. ")
    public void requestPasswordChangeViaMailWithValidData() {
        var req = new PasswordResetRequest();
        req.setEmail(testMail);
        req.setUsername(testUsername);
        SingleUseRequest request = singleUseRequestService.passwordResetRequest(req);
        assertNotNull(request, "La respuesta fue nula. ");
        assertTrue(request.isActive(), "Generó una solicitud ya expirada. ");
        assertNotEquals(request.getHash(), "", "Generó un hash vacío. ");
    }

    @Test
    @Order(2)
    @DisplayName("Solicitar cambio de contraseña: Correo válido, nombre de usuario inexistente. ")
    public void requestPasswordChangeViaMailWithInvalidUsername() {
        var req = new PasswordResetRequest();
        req.setEmail(testMail);
        req.setUsername(testUsername + "prefixThatMakeThisMailNonExistent");
        assertThrows(
                Fail.class,
                () -> singleUseRequestService.passwordResetRequest(req),
                "No lanza error cuando se envía un nombre de usuario no existente. "
        );
    }

    @Test
    @Order(3)
    @DisplayName("Solicitar cambio de contraseña: Correo inválido. ")
    public void requestPasswordChangeViaMailWithInvalidMail() {
        var req = new PasswordResetRequest();
        req.setEmail(testMail + "prefixThatMakeThisMailNonExistent");
        req.setUsername(testUsername);
        assertThrows(Fail.class, () -> {
            singleUseRequestService.passwordResetRequest(req);
        }, "No lanza error cuando se envía un correo no existente. ");
    }

    @Test
    @Order(4)
    @DisplayName("Solicitar cambio de contraseña: Sólo nombre de usuario. ")
    public void requestPasswordChangeViaMailWithUsernameOnly() {
        var req = new PasswordResetRequest();
        req.setUsername(testUsername);
        assertThrows(Fail.class, () -> {
            singleUseRequestService.passwordResetRequest(req);
        }, "No lanza error cuando se envía únicamente un nombre de usuario. ");
    }



    @Test
    @Order(5)
    @DisplayName("Solicitar cambio de contraseña: Número de teléfono + datos válidos. ")
    public void requestPasswordChangeViaPhoneWithValidData() {
        var req = new PasswordResetRequest();
        req.setPhone(testPhone);
        req.setUsername(testUsername);
        SingleUseRequest request = singleUseRequestService.passwordResetRequest(req);
        this.requestId = request.getId();
        assertNotNull(request, "La respuesta fue nula. ");
        assertTrue(request.isActive(), "Generó una solicitud ya expirada. ");
        assertNotEquals(request.getHash(), "", "Generó un hash vacío. ");
    }

    @Test
    @Order(6)
    @DisplayName("Solicitar cambio de contraseña: Número de teléfono válido, nombre de usuario inexistente. ")
    public void requestPasswordChangeViaPhoneWithInvalidUsername() {
        var req = new PasswordResetRequest();
        req.setPhone(testPhone);
        req.setUsername(testUsername + "prefixThatMakeThisMailNonExistent");
        assertThrows(
                Fail.class,
                () -> singleUseRequestService.passwordResetRequest(req),
                "No lanza error cuando se envía un nombre de usuario no existente. "
        );
    }

    @Test
    @Order(7)
    @DisplayName("Solicitar cambio de contraseña: Número de teléfono inválido. ")
    public void requestPasswordChangeViaPhoneWithInvalidPhone() {
        var req = new PasswordResetRequest();
        req.setPhone(testPhone + "prefixThatMakeThisMailNonExistent");
        req.setUsername(testUsername);
        assertThrows(Fail.class, () -> {
            singleUseRequestService.passwordResetRequest(req);
        }, "No lanza error cuando se envía un número de teléfono no existente. ");
    }

    @Test
    @Order(8)
    @DisplayName("Cambiar contraseña mediante OTP SMS, con código incorrecto. ")
    public void changePasswordInvalidCode() {
        var req = new PasswordResetAttempt();
        req.setNewPassword(testNewPassword);
        req.setId(requestId);
        req.setCode("111110");
        assertThrows(Fail.class, () -> {
            singleUseRequestService.passwordResetAttempt(req);
        }, "No lanza error cuando se envía un código incorrecto. ");
    }

    @Test
    @Order(9)
    @DisplayName("Cambiar contraseña mediante OTP SMS. ")
    public void changePassword() {
        var req = new PasswordResetAttempt();
        req.setNewPassword(testNewPassword);
        req.setId(requestId);
        req.setCode("111111");
        var opt = singleUseRequestRepository.findById(requestId);
        assertTrue(opt.isPresent(), "No carga bien el ID. ");
        var doc = opt.get();
        assertTrue(passwordEncoder.matches(req.getCode(), doc.getHash()), "El código no coincide. ");
        var res = singleUseRequestService.passwordResetAttempt(req);
        assertNotNull(res, "La respuesta fue nula. ");
        assertFalse(res.isTotpRequired(), "Pide autenticación por TOTP. ");
        assertNotNull(res.getToken(), "Envió un token nulo. ");
        assertFalse(res.getToken().isBlank(), "Envió un token en blanco. ");
    }

}
