package frgp.utn.edu.ar.quepasa.service;

import de.taimos.totp.TOTP;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.VerificationRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import frgp.utn.edu.ar.quepasa.utils.JwtTestUtils;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Servicio de autenticación")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationServiceTests {

    @Autowired private AuthenticationServiceImpl authenticationService;
    @Autowired private UserRepository userRepository;
    @Autowired UserService userService;
    private final String testUsername = "test5";
    private final String testPassword = "4nT0n10.¿0N$413Z";
    public User ANTONIO_GONZALEZ;

    @BeforeAll
    public void setup() {
        try {
            var opt = userRepository.findByUsername(testUsername);
            opt.ifPresent(user -> userRepository.delete(user));
        } catch(Exception expected) { }
        User e = new User();
        try {
            e = userService.findByUsername("antonio.gonzalez.ok");
        } catch(UsernameNotFoundException ex) {
            SignUpRequest req = new SignUpRequest();
            req.setUsername("antonio.gonzalez.ok");
            req.setPassword(testPassword);
            req.setName("Antonio González");
            req.setNeighbourhoodId(1L);
            authenticationService.signup(req);
            e = userService.findByUsername("antonio.gonzalez.ok");
            if(e.hasTotpEnabled()) {
                e.setTotp("no-totp");
                e = userRepository.save(e);
            }
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
    @Order(1)
    @DisplayName("Registrar un usuario")
    public void testSignup() {
        SignUpRequest req = new SignUpRequest();
        req.setUsername(testUsername);
        req.setPassword(testPassword);
        req.setNeighbourhoodId(1L);
        req.setName("Usuario de prueba");
        var res = new AtomicReference<JwtAuthenticationResponse>();
        assertDoesNotThrow(() -> {
            res.set(authenticationService.signup(req));
        }, "Error al intentar registrar. ");
        assertNotNull(res, "Respuesta de registro es nula. ");
        assertTrue(JwtTestUtils.isJwt(res.get().getToken()), "No genera un token JWT válido. ");

    }

    @Test
    @DisplayName("Registrar un usuario: Mala contraseña")
    public void testSignupWithBadPassword() {
        SignUpRequest req = new SignUpRequest();
        req.setUsername("maximo.canedo");
        req.setPassword("Maximo999");
        req.setNeighbourhoodId(1L);
        req.setName("Usuario de prueba");
        var res = new AtomicReference<JwtAuthenticationResponse>(null);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            res.set(authenticationService.signup(req));
        }, "Error al intentar registrar. ");
        assertNull(res.get(), "Respuesta de registro es nula. ");
    }

    @Test
    @DisplayName("Registrar un usuario: Mal nombre de usuario")
    public void testSignupWithBadUsername() {
        SignUpRequest req = new SignUpRequest();
        req.setUsername(".r..__ok.");
        req.setPassword("Maximo999");
        req.setNeighbourhoodId(1L);
        req.setName("Usuario de prueba");
        var res = new AtomicReference<JwtAuthenticationResponse>(null);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            res.set(authenticationService.signup(req));
        }, "Error al intentar registrar. ");
        assertNull(res.get(), "Respuesta de registro es nula. ");
    }

    @Test
    @Order(2)
    @DisplayName("Iniciar sesión")
    public void testLogin() {
        SigninRequest req = new SigninRequest();
        req.setUsername(ANTONIO_GONZALEZ.getUsername());
        // Esta cuenta fue creada antes de agregar las validaciones a las contraseñas;
        // Si se vuelve a crear, cambiar esto por "testPassword".
        req.setPassword(ANTONIO_GONZALEZ.getUsername());
        JwtAuthenticationResponse res = authenticationService.login(req);
        assertNotNull(res, "Respuesta de login es nula. ");
        assertTrue(JwtTestUtils.isJwt(res.getToken()), "No genera un token JWT válido. ");
    }

    @Test
    @Order(3)
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
    @Order(4)
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
    @Order(5)
    @DisplayName("Solicitar código de verificación por mail: Datos válidos")
    @WithMockUser("antonio.gonzalez.ok")
    public void requestVerificationCodeViaMail__validCase() throws MessagingException {
        var vr = new VerificationRequest();
        vr.setSubject("fake.mail@fake.fake");
        authenticationService.requestMailVerificationCode(vr);
    }

    @Test
    @Order(6)
    @DisplayName("Generación de código secreto para autenticación con OTP.")
    void generateSecret() {
        var data = authenticationService.generateSecret("username");
        assertNotNull(data);
        assertEquals("QuePasa", data.getIssuer());
        assertEquals(data.getUser(), "username");
        assertNotNull(data.getSecret());
    }

    @Test
    @Order(7)
    @DisplayName("Habilitar TOTP")
    @WithMockUser("antonio.gonzalez.ok")
    void enableTotp() {
        assertFalse(ANTONIO_GONZALEZ.hasTotpEnabled(), "TOTP ya estaba habilitado.");

        var secret = authenticationService.createTotpSecret();
        assertNotNull(secret, "No se generó el secret.");
        assertNotEquals(0, secret.length, "El secret está vacío.");

        var user = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(user.isPresent(), "Usuario no encontrado.");

        ANTONIO_GONZALEZ = user.get();
        assertTrue(ANTONIO_GONZALEZ.hasTotpEnabled(), "No se habilitó TOTP para el usuario.");
    }

    @Test
    @Order(8)
    @DisplayName("Generar y validar código TOTP")
    @WithMockUser("antonio.gonzalez.ok")
    void generateAndValidateTotpCode() {
        var user = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(user.isPresent(), "Usuario no encontrado.");
        ANTONIO_GONZALEZ = user.get();
        assertTrue(ANTONIO_GONZALEZ.hasTotpEnabled(), "TOTP deshabilitado. ");
        var totpCode = TOTP.getOTP(ANTONIO_GONZALEZ.getTotp());
        assertNotNull(totpCode, "No se generó el código TOTP.");
        assertFalse(totpCode.isEmpty(), "El código TOTP está vacío.");
        assertFalse(totpCode.isBlank(), "El código TOTP es solo espacios en blanco.");

        Pattern pattern = Pattern.compile("^\\d{6}$");
        Matcher matcher = pattern.matcher(totpCode);
        assertTrue(matcher.matches(), "El código TOTP no contiene exactamente 6 dígitos.");
    }

    @Test
    @Order(9)
    @DisplayName("Iniciar sesión con TOTP válido")
    @WithMockUser("antonio.gonzalez.ok")
    void loginWithValidTotp() {
        assertTrue(ANTONIO_GONZALEZ.hasTotpEnabled(), "TOTP se deshabilitó. ");
        var totpCode = TOTP.getOTP(ANTONIO_GONZALEZ.getTotp());
        assertTrue(authenticationService.validateTOTP(ANTONIO_GONZALEZ.getTotp(), totpCode), "El código TOTP no es válido.");

        var authResult = authenticationService.loginWithTotp(totpCode);
        assertNotNull(authResult, "La autenticación con TOTP falló.");
        assertNotNull(authResult.getToken(), "No se generó un token.");
        assertFalse(authResult.getToken().isBlank(), "El token generado está vacío.");
        assertFalse(authResult.isTotpRequired(), "Se sigue requiriendo TOTP después de la autenticación.");
    }

    @Test
    @Order(10)
    @DisplayName("Deshabilitar TOTP")
    @WithMockUser("antonio.gonzalez.ok")
    void disableTotp() {
        assertTrue(ANTONIO_GONZALEZ.hasTotpEnabled(), "TOTP se deshabilitó. ");
        authenticationService.disableTotp();

        var user = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(user.isPresent(), "Usuario no encontrado.");

        ANTONIO_GONZALEZ = user.get();
        assertFalse(ANTONIO_GONZALEZ.hasTotpEnabled(), "TOTP no se deshabilitó correctamente.");
    }

    @Test
    @Order(11)
    @DisplayName("Test integral de TOTP.")
    @WithMockUser("antonio.gonzalez.ok")
    void totpGeneralTest() {
        var user = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(user.isPresent());
        ANTONIO_GONZALEZ = user.get();
        if(ANTONIO_GONZALEZ.hasTotpEnabled()) {
            authenticationService.disableTotp();
        }
        user = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(user.isPresent());
        ANTONIO_GONZALEZ = user.get();
        // Habilitar
        assertFalse(ANTONIO_GONZALEZ.hasTotpEnabled());
        var x = authenticationService.createTotpSecret();
        assertNotNull(x);
        assertNotEquals(0, x.length);
        var opt = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(opt.isPresent());
        ANTONIO_GONZALEZ = opt.get();
        assertTrue(ANTONIO_GONZALEZ.hasTotpEnabled());
        // Obtener TOTP
        var totpCode = TOTP.getOTP(ANTONIO_GONZALEZ.getTotp());
        assertNotNull(totpCode);
        assertFalse(totpCode.isEmpty());
        assertFalse(totpCode.isBlank());
        Pattern p = Pattern.compile("^\\d{6}$");
        Matcher m = p.matcher(totpCode);
        assertTrue(m.matches());
        // Iniciar sesión
        assertTrue(authenticationService.validateTOTP(ANTONIO_GONZALEZ.getTotp(), totpCode));
        var authResult = authenticationService.loginWithTotp(totpCode);
        assertNotNull(authResult);
        assertNotNull(authResult.getToken());
        assertFalse(authResult.getToken().isBlank());
        assertFalse(authResult.isTotpRequired());
        // Deshabilitar
        authenticationService.disableTotp();
        opt = userRepository.findByUsername(ANTONIO_GONZALEZ.getUsername());
        assertTrue(opt.isPresent());
        ANTONIO_GONZALEZ = opt.get();
        assertFalse(ANTONIO_GONZALEZ.hasTotpEnabled());
    }


    @Test
    @Order(12)
    @DisplayName("Obtener usuario autenticado")
    @WithMockUser("antonio.gonzalez.ok")
    public void getAuthenticatedUser() {
        assertDoesNotThrow(() -> {
            authenticationService.getCurrentUserOrDie();
        });
    }

    @Test
    @Order(13)
    @DisplayName("Obtener usuario autenticado sin sesión activa. ")
    public void getAuthenticatedUserWithoutSession() {
        assertThrows(Exception.class, () -> {
            authenticationService.getCurrentUserOrDie();
        });
    }

}
