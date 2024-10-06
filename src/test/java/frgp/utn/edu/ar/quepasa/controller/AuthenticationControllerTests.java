package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import de.taimos.totp.TOTP;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.CodeVerificationRequest;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetAttempt;
import frgp.utn.edu.ar.quepasa.data.request.auth.PasswordResetRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.auth.SingleUseRequest;
import frgp.utn.edu.ar.quepasa.repository.MailRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationControllerTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private MailRepository mailRepository;
    @Autowired private AuthenticationService authenticationService;

    private String token = "";

    @BeforeAll
    public void cleanPossibleMockUsers() {
        var i = userRepository.findByUsername("mockUser0001");
        if(i.isEmpty()) {
            var req = new SignUpRequest();
            req.setUsername("mockUser0001");
            req.setPassword("P455w0&d+");
            req.setName("Usuario de prueba para inicio de sesión. ");
            req.setNeighbourhoodId(1);
            this.token = authenticationService.signup(req).getToken();
        } else {
            User e = i.get();
            e.setTotp("no-totp");
            userRepository.save(e);
            var req = new SigninRequest();
            req.setUsername("mockUser0001");
            req.setPassword("M4x%$4gv4nt320rb4");
            this.token = authenticationService.login(req).getToken();
        }
        if(userRepository.findByUsername("mockUser0123").isPresent()) {
            userRepository.delete(userRepository.findByUsername("mockUser0123").get());
        }
        if(userRepository.findByUsername("test.0034").isPresent()) {
            userRepository.delete(userRepository.findByUsername("test.0034").get());
        }
    }

    @AfterAll
    public void cleanMockUsers() {
        if(userRepository.findByUsername("mockUser0123").isPresent()) {
            userRepository.delete(userRepository.findByUsername("mockUser0123").get());
        }
        if(userRepository.findByUsername("test.0034").isPresent()) {
            userRepository.delete(userRepository.findByUsername("test.0034").get());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Registro de usuario con valores válidos")
    public void testSignUp() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("mockUser0123");
        request.setPassword("Correct.#Passw0rd");
        request.setNeighbourhoodId(1);
        request.setName("Usuario de prueba de controlador");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/signup")
                        .contentType("application/json")
                        .content(json)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());

    }

    @Test
    @Order(2)
    @DisplayName("Registro de usuario con nombre de usuario no disponible")
    public void testSignUp__usernameNotAvailable() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("root");
        request.setPassword("Correct.#Passw0rd");
        request.setNeighbourhoodId(1);
        request.setName("Usuario de prueba de controlador");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/api/signup")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("username"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("Registro de usuario con contraseña inválida")
    public void testSignUp_badPassword() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("test.0034");
        request.setPassword("adfafa");
        request.setNeighbourhoodId(1);
        request.setName("Usuario de prueba de controlador");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/api/signup")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("password"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Registro de usuario con nombre de usuario inválido")
    public void testSignUp_badUsername() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername(".beep..boop__.ok_");
        request.setPassword("adfafa");
        request.setNeighbourhoodId(1);
        request.setName("Usuario de prueba de controlador");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/api/signup")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("username"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("Inicio de sesión con valores válidos")
    public void testLogin() throws Exception {
        var credentials = new SigninRequest();
        credentials.setUsername("mockUser0001");
        credentials.setPassword("M4x%$4gv4nt320rb4");
        String body = objectMapper.writeValueAsString(credentials);
        mockMvc.perform(
                post("/api/login")
                    .contentType("application/json")
                    .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.field").doesNotExist())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    @Order(5)
    @DisplayName("Inicio de sesión con nombre de usuario no existente")
    public void testLogin__badUsername() throws Exception {
        var credentials = new SigninRequest();
        credentials.setUsername("mockUser0001__nonExistentUser");
        credentials.setPassword("P455w0&d+");
        String body = objectMapper.writeValueAsString(credentials);
        mockMvc.perform(
                        post("/api/login")
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @Order(6)
    @DisplayName("Inicio de sesión con contraseña inválida")
    public void testLogin__badPassword() throws Exception {
        var credentials = new SigninRequest();
        credentials.setUsername("mockUser0001");
        credentials.setPassword("P455w0&d+.......");
        String body = objectMapper.writeValueAsString(credentials);
        mockMvc.perform(
                        post("/api/login")
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @Order(7)
    @DisplayName("Inicio de sesión con credenciales vacías")
    public void testLogin__emptyCredentials() throws Exception {
        var credentials = new SigninRequest();
        credentials.setUsername("");
        credentials.setPassword("");
        String body = objectMapper.writeValueAsString(credentials);
        mockMvc.perform(
                        post("/api/login")
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @Order(8)
    @DisplayName("Solicitar código de verificación por correo: Datos válidos")
    public void testMV__ok() throws Exception {
        String body = "maximo.tomas.canedo@gmail.com";
        mockMvc.perform(
                post("/api/users/me/mail")
                        .contentType("text/plain")
                        .content(body)
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mail").exists())
                .andExpect(jsonPath("$.mail").value(body))
                .andExpect(jsonPath("$.verified").exists())
                .andExpect(jsonPath("$.requestedAt").exists())
                .andExpect(jsonPath("$.requestedAt").isNotEmpty());

    }

    @Test
    @Order(9)
    @DisplayName("Solicitar código de verificación por correo: Repetir correo")
    public void testMV__repeatMail() throws Exception {
        String body = "maximo.tomas.canedo@gmail.com";
        mockMvc.perform(
                        post("/api/users/me/mail")
                                .contentType("text/plain")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

    }

    @Test
    @Order(10)
    @DisplayName("Solicitar código de verificación por correo: Sin correo")
    public void testMV__noMail() throws Exception {
        String body = "m";
        mockMvc.perform(
                        post("/api/users/me/mail")
                                .contentType("text/plain")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("mail"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());

    }

    @Test
    @Order(11)
    @DisplayName("Solicitar código de verificación por correo: Sin autenticar")
    public void testMV__unauthenticated() throws Exception {
        String body = "maximo.tomas.canedo@gmail.com";
        mockMvc.perform(
                        post("/api/users/me/mail")
                                .contentType("text/plain")
                                .content(body)
                )
                .andExpect(status().is4xxClientError());
    }




    @Test
    @Order(12)
    @DisplayName("Solicitar código de verificación por número de teléfono: Datos válidos")
    public void testPV__ok() throws Exception {
        String body = "+541130388784";
        mockMvc.perform(
                        post("/api/users/me/phone")
                                .contentType("text/plain")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.phone").value(body))
                .andExpect(jsonPath("$.verified").exists())
                .andExpect(jsonPath("$.requestedAt").exists())
                .andExpect(jsonPath("$.requestedAt").isNotEmpty());

    }


    @Test
    @Order(13)
    @DisplayName("Verificar número de teléfono: Datos inválidos")
    public void testPC__wrongCode() throws Exception {
        var ver = new CodeVerificationRequest();
        ver.setSubject("+541130388784");
        ver.setCode("998997");
        String body = objectMapper.writeValueAsString(ver);
        mockMvc.perform(
                        post("/api/users/me/phone/verify")
                                .contentType("application/json")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.verified").doesNotExist())
                .andExpect(jsonPath("$.requestedAt").doesNotExist())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

    @Test
    @Order(14)
    @DisplayName("Verificar número de teléfono: Datos válidos")
    public void testPC__ok() throws Exception {
        var ver = new CodeVerificationRequest();
        ver.setSubject("+541130388784");
        ver.setCode("111111");
        String body = objectMapper.writeValueAsString(ver);
        mockMvc.perform(
                        post("/api/users/me/phone/verify")
                                .contentType("application/json")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.phone").value(ver.getSubject()))
                .andExpect(jsonPath("$.verified").exists())
                .andExpect(jsonPath("$.requestedAt").exists())
                .andExpect(jsonPath("$.requestedAt").isNotEmpty())
                .andExpect(jsonPath("$.verifiedAt").exists())
                .andExpect(jsonPath("$.verifiedAt").isNotEmpty());

    }


    @Test
    @Order(15)
    @DisplayName("Solicitar código de verificación por número de teléfono: Repetir número")
    public void testPV__repeatMail() throws Exception {
        String body = "+541130388784";
        mockMvc.perform(
                        post("/api/users/me/phone")
                                .contentType("text/plain")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

    }

    @Test
    @Order(16)
    @DisplayName("Solicitar código de verificación por número de teléfono: Sin número")
    public void testMV__noPhone() throws Exception {
        String body = "";
        mockMvc.perform(
                        post("/api/users/me/phone")
                                .contentType("text/plain")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is4xxClientError());

    }

    @Test
    @Order(17)
    @DisplayName("Solicitar código de verificación por número de teléfono: Número inválido")
    public void testPV__invalidPhone() throws Exception {
        String body = "543";
        mockMvc.perform(
                        post("/api/users/me/phone")
                                .contentType("text/plain")
                                .content(body)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.phone").doesNotExist())
                .andExpect(jsonPath("$.verified").doesNotExist())
                .andExpect(jsonPath("$.requestedAt").doesNotExist())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").value("phone"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray());

    }

    @Test
    @Order(18)
    @DisplayName("Solicitar código de verificación por número de teléfono: Sin autenticar")
    public void testPV__unauthenticated() throws Exception {
        String body = "+541130388784";
        mockMvc.perform(
                        post("/api/users/me/phone")
                                .contentType("text/plain")
                                .content(body)
                )
                .andExpect(status().is4xxClientError());
    }


    @Test
    @Order(19)
    @DisplayName("Habilitar TOTP")
    public void testEnableTOTP() throws Exception {
        mockMvc.perform(
                post("/api/users/me/totp")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }

    @Test
    @Order(20)
    @DisplayName("Iniciar sesión Post TOTP")
    public void testLoginTOTP() throws Exception {
        var opt = userRepository.findByUsername("mockUser0001");
        assertTrue(opt.isPresent());
        var user = opt.get();
        var rq = new SigninRequest();
        rq.setUsername("mockUser0001");
        rq.setPassword("M4x%$4gv4nt320rb4");
        MvcResult res = mockMvc.perform(
                        post("/api/login")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(rq))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.totpRequired").exists())
                .andExpect(jsonPath("$.totpRequired").value(true))
                .andReturn();
        String response = res.getResponse().getContentAsString();
        String partialToken = JsonPath.parse(response).read("$.token");
        var code = TOTP.getOTP(user.getTotp());
        mockMvc.perform(
                post("/api/login/totp")
                        .header("Authorization", "Bearer " + partialToken)
                        .contentType("text/plain")
                        .content(code)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.totpRequired").exists())
                .andExpect(jsonPath("$.totpRequired").value(false))
                .andReturn();
    }

    @Test
    @Order(21)
    @DisplayName("Deshabilitar TOTP")
    public void testDisableTOTP() throws Exception {
        mockMvc.perform(
                        delete("/api/users/me/totp")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    @Test
    @Order(22)
    @DisplayName("Solicitar cambio de contraseña")
    public void testRequirePasswordReset() throws Exception {
        var req = new PasswordResetRequest();
        req.setUsername("mockUser0001");
        req.setEmail("maximo.tomas.2@gmail.com");
        mockMvc.perform(
                post("/api/recover")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
    }

    @Test
    @Order(23)
    @DisplayName("Solicitar cambio de contraseña: Correo incorrecto")
    public void testRequirePasswordResetBadMail() throws Exception {
        var req = new PasswordResetRequest();
        req.setUsername("mockUser0001");
        req.setEmail("nonExistentmail@gmail.com");
        var performance = mockMvc.perform(
                        post("/api/recover")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
    }

    @Test
    @Order(24)
    @DisplayName("Solicitar cambio de contraseña: Sólo nombre de usuario")
    public void testRequirePasswordResetOnlyUsername() throws Exception {
        var req = new PasswordResetRequest();
        req.setUsername("mockUser0001");
        var performance = mockMvc.perform(
                        post("/api/recover")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
    }

    @Test
    @Order(25)
    @DisplayName("Solicitar cambio de contraseña por número de teléfono")
    public void testRequirePasswordResetWithPhone() throws Exception {
        var req = new PasswordResetRequest();
        req.setUsername("mockUser0001");
        req.setPhone("+541130388785");
        var performance = mockMvc.perform(
                        post("/api/recover")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
        var content = performance.getResponse().getContentAsString();
        SingleUseRequest obj = objectMapper.readValue(content, SingleUseRequest.class);
        UUID passwordResetID = obj.getId();
        var lastReq = new PasswordResetAttempt();
        lastReq.setId(passwordResetID);
        lastReq.setNewPassword("M4x%$4gv4nt320rb4");
        lastReq.setCode("111111");
        mockMvc.perform(
                post("/api/recover/reset")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(lastReq))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.totpRequired").exists())
                .andExpect(jsonPath("$.totpRequired").value(false));

        lastReq.setCode("010101");
        mockMvc.perform(
                post("/api/recover/reset")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(lastReq))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

    @Test
    @Order(26)
    @DisplayName("Obtener usuario autenticado")
    public void testGetAuthenticatedUser() throws Exception {
        mockMvc.perform(
                get("/api/users/me")
                        .header("Authorization", "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.username").value("mockUser0001"));
    }

}
