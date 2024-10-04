package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.utils.JwtTestUtils;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationServiceTests {

    @Mock private MailSenderService mailSenderService;
    @Autowired private AuthenticationServiceImpl authenticationService;
    @Autowired private UserRepository userRepository;
    @Autowired UserService userService;
    private final String testUsername = "test5";

    public User ANTONIO_GONZALEZ;

    @BeforeAll
    public void setup() {
        User e = new User();
        try {
            e = userService.findByUsername("antonio.gonzalez.ok");
        } catch(UsernameNotFoundException ex) {
            SignUpRequest req = new SignUpRequest();
            req.setUsername("antonio.gonzalez.ok");
            req.setPassword("antonio.gonzalez.ok");
            req.setName("Antonio González");
            req.setNeighbourhoodId(1);
            authenticationService.signup(req);
            e = userService.findByUsername("antonio.gonzalez.ok");
        } finally {
            ANTONIO_GONZALEZ = e;
        }
    }


    @AfterAll
    public void destroy() {
        try {
            userRepository.deleteByUsername(ANTONIO_GONZALEZ.getUsername());
            userRepository.deleteByUsername(testUsername);
        } catch(Exception expected) {}
    }

    @Test
    @DisplayName("Registrar un usuario")
    public void testSignup() {
        SignUpRequest req = new SignUpRequest();
        req.setUsername(testUsername);
        String testPassword = "Abc.1234";
        req.setPassword(testPassword);
        req.setNeighbourhoodId(1);
        req.setName("Usuario de prueba 1");
        JwtAuthenticationResponse res = authenticationService.signup(req);
        assertNotNull(res, "Respuesta de registro es nula. ");
        assertTrue(JwtTestUtils.isJwt(res.getToken()), "No genera un token JWT válido. ");

    }

    @Test
    @DisplayName("Iniciar sesión")
    public void testLogin() {
        SigninRequest req = new SigninRequest();
        req.setUsername(ANTONIO_GONZALEZ.getUsername());
        req.setPassword(ANTONIO_GONZALEZ.getUsername()); // La contraseña es idéntica al nombre de usuario, para más simplicidad.
        JwtAuthenticationResponse res = authenticationService.login(req);
        assertNotNull(res, "Respuesta de login es nula. ");
        assertTrue(JwtTestUtils.isJwt(res.getToken()), "No genera un token JWT válido. ");
    }

    @Test
    @DisplayName("Iniciar sesión con contraseña incorrecta")
    public void testLoginBadCredentials() {
        SigninRequest req = new SigninRequest();
        req.setUsername(ANTONIO_GONZALEZ.getUsername());
        req.setPassword("Abc.12345");
        boolean ok = false;
        try {
            JwtAuthenticationResponse res = authenticationService.login(req);
            assertNull(res, "Generó respuesta de inicio de sesión con credenciales incorrectas. ");
        } catch(IllegalArgumentException | BadCredentialsException expected) {
            ok = true;
        }
        assertTrue(ok, "Permite iniciar sesión con contraseña inválida. ");
    }

    @Test
    @DisplayName("Iniciar sesión con nombre de usuario inexistente")
    public void testLoginBadUsername() {
        SigninRequest req = new SigninRequest();
        req.setUsername("nonExistingUsername");
        req.setPassword("Abc.1234");
        boolean ok = false;
        try {
            JwtAuthenticationResponse res = authenticationService.login(req);
            assertNull(res, "Generó respuesta de inicio de sesión con credenciales incorrectas. ");
        } catch(IllegalArgumentException | BadCredentialsException expected) {
            ok = true;
        }
        assertTrue(ok, "Permite iniciar sesión con el nombre de usuario de una cuenta inexistente. ");
    }

    @Test
    @DisplayName("Solicitar código de verificación por mail: Datos válidos")
    public void requestVerificationCodeViaMail__validCase() throws MessagingException {
        var vr = new VerificationRequest();
        vr.setSubject("fake.mail@fake.fake");
        authenticationService.requestMailVerificationCode(vr);
    }

}
