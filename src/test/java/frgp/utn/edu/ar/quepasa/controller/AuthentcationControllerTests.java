package frgp.utn.edu.ar.quepasa.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthentcationControllerTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @BeforeAll
    public void cleanPossibleMockUsers() {
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

}
