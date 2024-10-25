package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.PostServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.VoteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Servicio de publicaciones")
public class PostServiceTests {

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private PostSubtypeRepository postSubtypeRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private NeighbourhoodRepository neighbourhoodRepository;
    @Autowired
    private PostServiceImpl service;
    @Mock
    private AuthenticationServiceImpl authenticationService;
    @Mock
    private VoteServiceImpl voteService;

    public PostServiceTests() { MockitoAnnotations.openMocks(this); }

    @Test
    @DisplayName("Buscar post por ID.")
    public void findById_PostFound_ReturnsPost() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        setAuthContext(username, "USER");

        Post mockPost = new Post();
        mockPost.setId(id);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        assertDoesNotThrow(() -> {
            Post foundPost = service.findById(id);
            assertNotNull(foundPost);
            assertEquals(id, foundPost.getId());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar post por ID, ID inexistente.")
    public void findById_PostNotFound_ThrowsException() {
        Integer id = 955;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        setAuthContext(username, "USER");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> service.findById(id));

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar posts.")
    public void findPosts_PostsFound_ReturnsPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        setAuthContext(username, "USER");

        Post post = new Post();
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findAll(pageable)).thenReturn(postPage);

        assertDoesNotThrow(() -> {
            Page<Post> posts = service.findAll(pageable, true);

            assertNotNull(posts);
            assertFalse(posts.isEmpty());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar posts por OP.")
    public void findByOp_PostsFound_ReturnsPosts() {
        Integer opId = 4;
        Pageable pageable = PageRequest.of(0, 10);
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.NEIGHBOUR);

        setAuthContext(username, "NEIGHBOUR");

        User op = new User();
        op.setId(opId);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(userRepository.findById(opId)).thenReturn(Optional.of(op));

        Post post = new Post();
        post.setOwner(op);
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findByOwner(op, pageable)).thenReturn(postPage);

        assertDoesNotThrow(() -> {
            Page<Post> foundPosts = service.findByOp(opId, pageable);

            assertNotNull(foundPosts);
            assertFalse(foundPosts.isEmpty());

            foundPosts.forEach(foundPost -> assertEquals(opId, foundPost.getOwner().getId()));
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Buscar posts por OP, OP inexistente.")
    public void findByOp_PostNotFound_ThrowsException() {
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.NEIGHBOUR);

        setAuthContext(username, "NEIGHBOUR");

        User op = new User();
        op.setId(955);
        Pageable pageable = PageRequest.of(0, 10);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findByOwner(op, pageable)).thenReturn(Page.empty());

        assertThrows(Fail.class, () -> service.findByOp(op.getId(), pageable));

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear post.")
    public void createPost_PostNew_ReturnsPost() {
        String username = "donald";
        Integer subId = 4;
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.NEIGHBOUR);

        setAuthContext(username, "NEIGHBOUR");

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.of(subtype));
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.of(neighbourhood));

        PostCreateRequest request = mockPostCreate(username, subId, neighId);

        assertDoesNotThrow(() -> {
            var saved = service.create(request, mockUser);
            assertNotNull(saved);
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear post, subtipo inexistente.")
    public void createPost_SubtypeNotFound_ThrowsException() {
        String username = "donald";
        Integer subId = 4;
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.NEIGHBOUR);

        setAuthContext(username, "NEIGHBOUR");

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.empty());
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.of(neighbourhood));

        PostCreateRequest request = mockPostCreate(username, subId, neighId);

        assertThrows(NoSuchElementException.class, () -> service.create(request, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Crear post, barrio inexistente.")
    public void createPost_NeighbourhoodNotFound_ThrowsException() {
        String username = "donald";
        Integer subId = 4;
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.NEIGHBOUR);

        setAuthContext(username, "NEIGHBOUR");

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.of(subtype));
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.empty());

        PostCreateRequest request = mockPostCreate(username, subId, neighId);

        assertThrows(NoSuchElementException.class, () -> service.create(request, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID.")
    public void updatePost_PostFound_GoodData() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        Post mockPost = mockPostCreateEdit(id, mockUser, new PostSubtype(), new Neighbourhood());

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = mockPostEdit(null, null);

        assertDoesNotThrow(() -> {
            var saved = service.update(id, request, mockUser);
            assertNotNull(saved);
            assertEquals(id, saved.getId());
        });

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, post inexistente.")
    public void updatePost_PostNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        var request = mockPostEdit(null, null);

        assertThrows(Fail.class, () -> service.update(id, request, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, subtipo inexistente.")
    public void updatePost_SubtypeNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        Integer subId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        Post mockPost = mockPostCreateEdit(id, mockUser, subtype, new Neighbourhood());

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.empty());
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = mockPostEdit(subId, null);

        assertThrows(NoSuchElementException.class, () -> service.update(id, request, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, barrio inexistente.")
    public void updatePost_NeighbourhoodNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        Post mockPost = mockPostCreateEdit(id, mockUser, new PostSubtype(), neighbourhood);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.empty());
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = mockPostEdit(null, neighId);

        assertThrows(NoSuchElementException.class, () -> service.update(id, request, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Modificar post por ID, permisos insuficientes.")
    public void updatePost_AccessDenied_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        String username2 = "ronald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        User mockUser2 = new User();
        mockUser2.setUsername(username2);
        mockUser2.setRole(Role.USER);

        setAuthContext(username2, "USER");

        Post mockPost = mockPostCreateEdit(id, mockUser, new PostSubtype(), new Neighbourhood());

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = mockPostEdit(null, null);

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.update(id, request, mockUser2));

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar post por ID.")
    public void deletePost_PostFound_ReturnsNoContent() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        assertDoesNotThrow(() -> service.delete(id, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar post por ID, ID inexistente.")
    public void deletePost_PostNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Fail.class, () -> service.delete(id, mockUser));

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar post por ID, permisos insuficientes.")
    public void deletePost_AccessDenied_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        String username2 = "ronald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        User mockUser2 = new User();
        mockUser2.setUsername(username2);
        mockUser2.setRole(Role.USER);

        setAuthContext(username2, "USER");

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.delete(id, mockUser2));

        clearAuthContext();
    }

    private PostCreateRequest mockPostCreate(String username, Integer subId, Long neighId) {
        PostCreateRequest request = new PostCreateRequest();
        request.setOriginalPoster(username);
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

    private void setAuthContext(String username, String role) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("password")
                .roles(role)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthContext() {
        SecurityContextHolder.clearContext();
    }

}