package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthenticationService authenticationService;

    private String token = "";

    @BeforeAll
    public void cleanPossibleMockUsers() {
        if(userRepository.findByUsername("mockUser0001").isEmpty()) {
            var req = new SignUpRequest();
            req.setUsername("mockUser0001");
            req.setPassword("P455w0&d+");
            req.setName("Usuario de prueba para inicio de sesión. ");
            req.setNeighbourhoodId(1);
            this.token = authenticationService.signup(req).getToken();
        } else {
            var req = new SigninRequest();
            req.setUsername("mockUser0001");
            req.setPassword("P455w0&d+");
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
    @DisplayName("Registro de usuario con nombre de usuario no disponible")
    public void testSignUp__usernameNotAvailable() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("root");
        request.setPassword("Correct.#Passw0rd");
        request.setNeighbourhoodId(1);
        request.setName("Usuario de prueba de controlador #2");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/api/signup")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Registro de usuario con contraseña inválida")
    public void testSignUp_badPassword() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("test.0034");
        request.setPassword("adfafa");
        request.setNeighbourhoodId(1);
        request.setName("Usuario de prueba de controlador #3");
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/api/signup")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Inicio de sesión con valores válidos")
    public void testLogin() throws Exception {
        var credentials = new SigninRequest();
        credentials.setUsername("mockUser0001");
        credentials.setPassword("P455w0&d+");
        String body = objectMapper.writeValueAsString(credentials);
        mockMvc.perform(
                post("/api/login")
                    .contentType("application/json")
                    .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
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
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

    @Test
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

}
