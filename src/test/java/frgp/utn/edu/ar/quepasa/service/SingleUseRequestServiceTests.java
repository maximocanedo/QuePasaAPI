package frgp.utn.edu.ar.quepasa.service;


import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.Mail;
import frgp.utn.edu.ar.quepasa.model.auth.Phone;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.PhoneRepository;
import frgp.utn.edu.ar.quepasa.repository.SingleUseRequestRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
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
    @Autowired private AuthenticationService authenticationService;
    @Autowired private NeighbourhoodRepository neighbourhoodRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private MailRepository mailRepository;
    @Autowired private PhoneRepository phoneRepository;

    private final String testUsername = "surs.test.0001." + System.currentTimeMillis();
    private final String testMail = "42684627.canedo@gmail.com";
    private final String testPhone = "+541130388784";
    private final String testPassword = "fUn1cV1i?Fvnl$u14€";
    private final String testNewPassword = "P4r14%P1ú%1r4";
    private UUID requestId;
    @Autowired
    private SingleUseRequestRepository singleUseRequestRepository;

    private User test;

    public User getTestUser() {
        if(test == null) return test;
        Optional<User> opt = userRepository.findByUsername(testUsername);
        if(opt.isEmpty()) throw new Fail("User test was nismaned. ");
        test = opt.get();
        return opt.get();
    }

    @BeforeAll
    public void setUp() {
        Optional<User> existingUser = userRepository.findByUsername(testUsername);
        existingUser.ifPresent(user -> {
            //singleUseRequestRepository.deleteAllInBatch();
            //userRepository.deleteAllInBatch(List.of(user));
        });

        Neighbourhood x = neighbourhoodRepository.findAll().getFirst();
        User u = new User();
        u.setUsername(testUsername);
        u.setPassword(passwordEncoder.encode(testPassword));
        u.setActive(true);
        u.setName("Usuario de prueba para solicitudes de único uso");
        u.setAddress("");
        u.setNeighbourhood(x);
        u.setRole(Role.USER);
        test = userService.save(u);

        Mail mail = new Mail();
        mail.setMail(testMail);
        mail.setVerified(true);
        mail.setUser(test);
        mail.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
        mail.setRequestedAt(new Timestamp(System.currentTimeMillis()));
        mail.setHash(passwordEncoder.encode("111111"));
        mailRepository.save(mail);

        Phone phone = new Phone();
        phone.setPhone(testPhone);
        phone.setVerified(true);
        phone.setUser(test);
        phone.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
        phone.setRequestedAt(new Timestamp(System.currentTimeMillis()));
        phone.setHash(passwordEncoder.encode("111111"));
        phoneRepository.save(phone);
    }


    @Test
    @Order(1)
    @DisplayName("Solicitar cambio de contraseña: Correo + datos válidos. ")
    public void requestPasswordChangeViaMailWithValidData() {
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
        var req = new PasswordResetRequest();
        req.setPhone(testPhone);
        req.setUsername(testUsername);
        SingleUseRequest request = singleUseRequestService.passwordResetRequest(req);
        this.requestId = request.getId();
        System.out.println("REQUEST ID = " + request.getId());
        assertNotNull(request, "La respuesta fue nula. ");
        assertTrue(request.isActive(), "Generó una solicitud ya expirada. ");
        assertNotEquals(request.getHash(), "", "Generó un hash vacío. ");
    }

    @Test
    @Order(6)
    @DisplayName("Solicitar cambio de contraseña: Número de teléfono válido, nombre de usuario inexistente. ")
    public void requestPasswordChangeViaPhoneWithInvalidUsername() {
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
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
        var user = getTestUser();
        assertNotNull(user);
        var req = new PasswordResetAttempt();
        req.setNewPassword(testNewPassword);
        req.setId(requestId);
        System.out.println("REQUEST ID IN 246= " + requestId);
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

    @Test
    @Order(10)
    @DisplayName("Intentar iniciar sesión con la contraseña anterior. ")
    public void tryToLoginWithOldPassword() {
        var user = getTestUser();
        assertNotNull(user);
        assertThrows(BadCredentialsException.class, () -> {
            var sr = new SigninRequest();
            sr.setUsername(testUsername);
            sr.setPassword(testPassword);
            var jwt = authenticationService.login(sr);
            assertNull(jwt);
            assertTrue(jwt.getToken().isBlank());
        }, "No realizó el cambió de contraseña. ");
    }

    @Test
    @Order(11)
    @DisplayName("Iniciar sesión luego del cambio de contraseña. ")
    public void loginPostPasswordReset() {
        var user = getTestUser();
        assertNotNull(user);
        assertDoesNotThrow(() -> {
            var sr = new SigninRequest();
            sr.setUsername(testUsername);
            sr.setPassword(testNewPassword);
            authenticationService.login(sr);
        }, "No permite iniciar sesión con las credenciales correctas luego de cambiar la contraseña. ");
    }

    @AfterAll
    public void endTests() {
    }
}
