package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controlador de subtipos de publicaciones")
public class PostSubtypeControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private final PostSubtypeRepository postSubtypeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public PostSubtypeControllerTests() {
        postSubtypeRepository = Mockito.mock(PostSubtypeRepository.class);
    }

    @Test
    @DisplayName("Buscar subtipo de post por ID.")
    public void searchPostSubtypeById() throws Exception {
        setAuthContext();

        when(postSubtypeRepository.findById(1)).thenReturn(Optional.of(new PostSubtype()));

        mockMvc.perform((get("/api/post-subtypes/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar subtipo de post por ID, ID inexistente.")
    public void searchPostSubtypeById_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/post-subtypes/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar subtipos de post.")
    public void searchPostSubtypes() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/post-subtypes/all"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar subtipos de post por tipo.")
    public void searchPostSubtypesByType() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/post-subtypes/type/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar subtipos de post por tipo, tipo no existente.")
    public void searchPostSubtypesByType_TypeNotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/post-subtypes/type/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear subtipo de post.")
    public void createPostSubtype() throws Exception {
        setAuthContext();

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        mockMvc.perform((post("/api/post-subtypes"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear subtipo de post, tipo inexistente.")
    public void createPostSubtype_TypeNotFound() throws Exception {
        setAuthContext();

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(909);
        request.setDescription("Entretenimiento");

        mockMvc.perform((post("/api/post-subtypes"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar subtipo de post por ID.")
    public void updatePostSubtype() throws Exception {
        setAuthContext();

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        mockMvc.perform((patch("/api/post-subtypes/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar subtipo de post por ID, ID inexistente.")
    public void updatePostSubtype_NotFound() throws Exception {
        setAuthContext();

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        mockMvc.perform((patch("/api/post-subtypes/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar subtipo de post por ID, tipo inexistente.")
    public void updatePostSubtype_TypeNotFound() throws Exception {
        setAuthContext();

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(909);
        request.setDescription("Entretenimiento");

        mockMvc.perform((patch("/api/post-subtypes/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar subtipo de post por ID.")
    public void deletePostSubtype() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/post-subtypes/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar subtipo de post por ID, ID inexistente.")
    public void deletePostSubtype_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/post-subtypes/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    private void setAuthContext() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("root")
                .password("123456789")
                .roles("ADMIN")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthContext() {
        SecurityContextHolder.clearContext();
    }

}
