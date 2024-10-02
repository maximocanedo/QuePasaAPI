package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.response.JwtAuthenticationResponse;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.records.UserRecords;
import frgp.utn.edu.ar.quepasa.utils.JwtTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static frgp.utn.edu.ar.quepasa.records.UserRecords.ANTONIO_GONZALEZ;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceTests {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired UserService userService;

    private final String testUsername = "test2";
    private final String testPassword = "Abc.1234";

    @Test
    @DisplayName("Registrar un usuario")
    public void testSignup() {
        try {
            userService.delete(testUsername);
        } catch(Exception expected) {} finally {
            SignUpRequest req = new SignUpRequest();
            req.setUsername(testUsername);
            req.setPassword(testPassword);
            req.setNeighbourhoodId(1);
            req.setName("Usuario de prueba 1");
            JwtAuthenticationResponse res = authenticationService.signup(req);
            assertNotNull(res, "Respuesta de registro es nula. ");
            assertTrue(JwtTestUtils.isJwt(res.getToken()), "No genera un token JWT válido. ");
        }
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



}
