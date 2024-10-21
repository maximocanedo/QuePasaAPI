package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
@DisplayName("Controlador de publicaciones")
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostSubtypeRepository postSubtypeRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public PostControllerTests() {
        postRepository = Mockito.mock(PostRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        postSubtypeRepository = Mockito.mock(PostSubtypeRepository.class);
        neighbourhoodRepository = Mockito.mock(NeighbourhoodRepository.class);
    }

    @BeforeAll
    public void setup() {

    }

    @Test
    @DisplayName("Buscar post por ID.")
    public void searchPostById() throws Exception {
        setAuthContext();

        when(postRepository.findById(1)).thenReturn(Optional.of(new Post()));

        mockMvc.perform((get("/api/posts/{id}", 1))
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar post por ID. ID inexistente.")
    public void searchPostById_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/posts/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Post not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar posts.")
    public void searchPosts() throws Exception {
        setAuthContext();

        Pageable pageable = PageRequest.of(0, 10);

        Post post = new Post();
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findAll(pageable)).thenReturn(postPage);

        mockMvc.perform((get("/api/posts/all"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar posts por OP.")
    public void searchPostsByOp() throws Exception {
        setAuthContext();

        Pageable pageable = PageRequest.of(0, 10);

        Integer opId = 1;
        User op = new User();
        op.setId(opId);

        when(userRepository.findById(opId)).thenReturn(Optional.of(op));

        Post post = new Post();
        post.setOwner(op);
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findByOwner(op, pageable)).thenReturn(postPage);

        mockMvc.perform((get("/api/posts/op/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar posts por OP, OP inexistente.")
    public void searchPostsByOp_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((get("/api/posts/op/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear post.")
    public void createPost() throws Exception {
        setAuthContext();

        Integer opId = 1;
        User op = new User();
        op.setId(opId);

        when(userRepository.findById(opId)).thenReturn(Optional.of(op));

        Integer subId = 1;
        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.of(subtype));

        long neighId = 1L;
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.of(neighbourhood));

        PostCreateRequest request = mockPostCreate(subId, neighId);

        mockMvc.perform((post("/api/posts"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear post, subtipo inexistente.")
    public void createPost_SubtypeNotFound() throws Exception {
        setAuthContext();

        Integer opId = 1;
        User op = new User();
        op.setId(opId);

        when(userRepository.findById(opId)).thenReturn(Optional.of(op));

        Integer subId = 909;

        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.empty());

        long neighId = 1L;
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.of(neighbourhood));

        PostCreateRequest request = mockPostCreate(subId, neighId);

        mockMvc.perform((post("/api/posts"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear post, barrio inexistente.")
    public void createPost_NeighbourhoodNotFound() throws Exception {
        setAuthContext();

        Integer opId = 1;
        User op = new User();
        op.setId(opId);

        when(userRepository.findById(opId)).thenReturn(Optional.of(op));

        Integer subId = 1;
        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.of(subtype));

        long neighId = 909;

        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.empty());

        PostCreateRequest request = mockPostCreate(subId, neighId);

        mockMvc.perform((post("/api/posts"))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID.")
    public void updatePost() throws Exception {
        setAuthContext();

        var request = mockPostEdit(1, 1L);

        mockMvc.perform((patch("/api/posts/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, post inexistente.")
    public void updatePost_NotFound() throws Exception {
        setAuthContext();

        var request = mockPostEdit(1, 1L);

        mockMvc.perform((patch("/api/posts/{id}", 909))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, subtipo inexistente.")
    public void updatePost_SubtypeNotFound() throws Exception {
        setAuthContext();

        var request = mockPostEdit(909, 1L);

        mockMvc.perform((patch("/api/posts/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, barrio inexistente.")
    public void updatePost_NeighbourhoodNotFound() throws Exception {
        setAuthContext();

        var request = mockPostEdit(1, 909L);

        mockMvc.perform((patch("/api/posts/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar post por ID.")
    public void deletePost() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/posts/{id}", 1))
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar post por ID, ID inexistente.")
    public void deletePost_NotFound() throws Exception {
        setAuthContext();

        mockMvc.perform((delete("/api/posts/{id}", 909))
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

    private PostCreateRequest mockPostCreate(Integer subId, Long neighId) {
        PostCreateRequest request = new PostCreateRequest();
        request.setOriginalPoster("donald");
        request.setAudience(Audience.NEIGHBORHOOD);
        request.setTitle("Busco mi mochila");
        request.setSubtype(subId);
        request.setDescription("Perdí mi mochila ayer, en el evento del día 08/10. Si alguien la vio agradezco cualquier información. Saludos");
        request.setNeighbourhood(neighId);
        request.setTimestamp(Timestamp.valueOf("2024-10-08 14:30:15.123456789"));
        request.setTags("extravio,mochila,evento-ayer");

        return request;
    }

    private Post mockPostCreateEdit(Integer id, User mockUser, PostSubtype subtype, Neighbourhood neighbourhood) {
        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        mockPost.setTitle("Siga los pasos de esta publicación y gane 1 millón de dólares");
        mockPost.setSubtype(subtype);
        mockPost.setDescription("Haga click aquí: www.1milliondollars100percentrealnofake.com");
        mockPost.setNeighbourhood(neighbourhood);
        mockPost.setTags("dollar,1 million,earndollars");

        return mockPost;
    }

    private PostPatchEditRequest mockPostEdit(Integer subId, Long neighId) {
        var request = new PostPatchEditRequest();
        request.setSubtype(subId);
        request.setDescription("Soy una descripcion");
        request.setNeighbourhood(neighId);
        request.setTags("descripcion,breve");

        return request;
    }

    private void clearAuthContext() {
        SecurityContextHolder.clearContext();
    }

}
