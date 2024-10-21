package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
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
@DisplayName("Controlador de tipos de publicaciones")
public class PostTypeControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private final PostTypeRepository postTypeRepository;

    public PostTypeControllerTests() {
        postTypeRepository = Mockito.mock(PostTypeRepository.class);
    }

    @BeforeAll
    public void setup() {

    }

    @Test
    @DisplayName("Buscar tipo de post por ID.")
    public void searchPostTypeById() throws Exception {
        setAuthContext();

        when(postTypeRepository.findById(1)).thenReturn(Optional.of(new PostType()));

        mockMvc.perform((get("/api/post-types/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar tipo de post por ID, ID inexistente.")
    public void searchPostTypeById_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/post-types/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar tipos de post.")
    public void searchPostTypes() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/post-types/all"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear tipo de post.")
    public void createPostType() throws Exception {
        setAuthContext();

        String description = "post-type1";

        mockMvc.perform((post("/api/post-types"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(description))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName(("Modificar tipo de post por ID."))
    public void updatePostType() throws Exception {
        setAuthContext();

        String description = "post-type1-new";

        mockMvc.perform((patch("/api/post-types/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(description))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName(("Modificar tipo de post por ID, de un tipo inexistente."))
    public void updatePostType_NotFound() throws Exception {
        setAuthContext();

        String description = "post-type1-new";

        mockMvc.perform((patch("/api/post-types/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(description))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar tipo de post por ID.")
    public void deletePostType() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/post-types/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar tipo de post por ID, ID inexistente.")
    public void deletePostType_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/post-types/{id}", 909))
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
