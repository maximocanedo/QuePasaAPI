package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
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
import frgp.utn.edu.ar.quepasa.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PostServiceTests {

    @Mock private PostRepository postRepository;
    @Mock private PostSubtypeRepository postSubtypeRepository;
    @Mock private UserRepository userRepository;
    @Mock private NeighbourhoodRepository neighbourhoodRepository;
    @InjectMocks private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Buscar post por ID.")
    void findById_PostFound_ReturnsPost() {
        Integer id = 1;
        Post mockPost = new Post();
        mockPost.setId(id);

        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        Post foundPost = postService.findById(id);
        assertNotNull(foundPost);
        assertEquals(id, foundPost.getId());
    }

    @Test
    @DisplayName("Buscar post por ID, ID inexistente.")
    void findById_PostNotFound_ThrowsException() {
        Integer id = 955;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.findById(id);
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("Buscar posts.")
    void findPosts_PostsFound_ReturnsPosts() {
        Pageable pageable = PageRequest.of(0, 10);

        Post post = new Post();
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findAll(pageable)).thenReturn(postPage);

        Page<Post> posts = postService.listPost(pageable);

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
    }

    @Test
    @DisplayName("Buscar posts por OP.")
    void findByOp_PostsFound_ReturnsPosts() {
        Integer opId = 4;
        Pageable pageable = PageRequest.of(0, 10);

        User op = new User();
        op.setId(opId);

        when(userRepository.findById(opId)).thenReturn(Optional.of(op));

        Post post = new Post();
        post.setOwner(op);
        Page<Post> postPage = new PageImpl<>(List.of(post));

        when(postRepository.findByOriginalPoster(op, pageable)).thenReturn(postPage);

        Page<Post> foundPosts = postService.findByOp(opId, pageable);

        assertNotNull(foundPosts);
        assertFalse(foundPosts.isEmpty());

        foundPosts.forEach(foundPost -> {
            assertEquals(opId, foundPost.getOwner().getId());
        });
    }

    @Test
    @DisplayName("Buscar posts por OP, OP inexistente.")
    void findByOp_PostNotFound_ThrowsException() {
        User op = new User();
        op.setId(955);
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.findByOriginalPoster(op, pageable)).thenReturn(Page.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.findByOp(op.getId(), pageable);
        });
    }

    @Test
    @DisplayName("Crear post.")
    void createPost_PostNew_ReturnsPost() {
        String username = "donald";
        Integer subId = 4;
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.of(subtype));
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.of(neighbourhood));

        PostCreateRequest request = new PostCreateRequest();
        request.setOriginalPoster(username);
        request.setAudience(Audience.NEIGHBORHOOD);
        request.setTitle("Busco mi mochila");
        request.setSubtype(subId);
        request.setDescription("Perdí mi mochila ayer, en el evento del día 08/10. Si alguien la vio agradezco cualquier información. Saludos");
        request.setNeighbourhood(neighId);
        request.setTimestamp(Timestamp.valueOf("2024-10-08 14:30:15.123456789"));
        request.setTags("extravio,mochila,evento-ayer");

        AtomicReference<Post> createdPost = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            createdPost.set(postService.create(request, mockUser));
        });
        assertNotNull(createdPost.get());
        assertEquals(username, createdPost.get().getOwner().getUsername());
        assertEquals(subId, createdPost.get().getSubtype().getId());
        assertEquals(neighId, createdPost.get().getNeighbourhood().getId());
    }

    @Test
    @DisplayName("Crear post, subtipo inexistente.")
    void createPost_SubtypeNotFound_ThrowsException() {
        String username = "donald";
        Integer subId = 4;
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.empty());
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.of(neighbourhood));

        PostCreateRequest request = new PostCreateRequest();
        request.setOriginalPoster(username);
        request.setAudience(Audience.NEIGHBORHOOD);
        request.setTitle("Busco mi mochila");
        request.setSubtype(subId);
        request.setDescription("Perdí mi mochila ayer, en el evento del día 08/10. Si alguien la vio agradezco cualquier información. Saludos");
        request.setNeighbourhood(neighId);
        request.setTimestamp(Timestamp.valueOf("2024-10-08 14:30:15.123456789"));
        request.setTags("extravio,mochila,evento-ayer");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            postService.create(request, mockUser);
        });

        assertEquals("Subtype not found", exception.getMessage());
    }

    @Test
    @DisplayName("Crear post, barrio inexistente.")
    void createPost_NeighbourhoodNotFound_ThrowsException() {
        String username = "donald";
        Integer subId = 4;
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.of(subtype));
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.empty());

        PostCreateRequest request = new PostCreateRequest();
        request.setOriginalPoster(username);
        request.setAudience(Audience.NEIGHBORHOOD);
        request.setTitle("Busco mi mochila");
        request.setSubtype(subId);
        request.setDescription("Perdí mi mochila ayer, en el evento del día 08/10. Si alguien la vio agradezco cualquier información. Saludos");
        request.setNeighbourhood(neighId);
        request.setTimestamp(Timestamp.valueOf("2024-10-08 14:30:15.123456789"));
        request.setTags("extravio,mochila,evento-ayer");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            postService.create(request, mockUser);
        });

        assertEquals("Neighbourhood not found", exception.getMessage());
    }

    @Test
    @DisplayName("Modificar post por ID.")
    void updatePost_PostFound_GoodData() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        mockPost.setTitle("Siga los pasos de esta publicación y gane 1 millón de dólares");
        mockPost.setSubtype(new PostSubtype());
        mockPost.setDescription("Haga click aquí: www.1milliondollars100percentrealnofake.com");
        mockPost.setTags("dollar,1 million,earndollars");

        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = new PostPatchEditRequest();
        request.setDescription("Soy una descripcion");
        request.setTags("descripcion,breve");

        AtomicReference<Post> foundPost = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            foundPost.set(postService.update(id, request, mockUser));
        });
        assertNotNull(foundPost.get());
        assertEquals(id, foundPost.get().getId());
        assertEquals("Soy una descripcion", foundPost.get().getDescription());
        assertEquals("descripcion,breve", foundPost.get().getTags());
    }

    @Test
    @DisplayName("Modificar post por ID, de un post no existente.")
    void updatePost_PostNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        mockPost.setTitle("Siga los pasos de esta publicación y gane 1 millón de dólares");
        mockPost.setSubtype(new PostSubtype());
        mockPost.setDescription("Haga click aquí: www.1milliondollars100percentrealnofake.com");
        mockPost.setTags("dollar,1 million,earndollars");

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        var request = new PostPatchEditRequest();
        request.setDescription("Soy una descripcion");
        request.setTags("descripcion,breve");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.update(id, request, mockUser);
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("Modificar post por ID, subtipo inexistente.")
    void updatePost_SubtypeNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        Integer subId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        PostSubtype subtype = new PostSubtype();
        subtype.setId(subId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(postSubtypeRepository.findById(subId)).thenReturn(Optional.empty());

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        mockPost.setTitle("Siga los pasos de esta publicación y gane 1 millón de dólares");
        mockPost.setSubtype(subtype);
        mockPost.setDescription("Haga click aquí: www.1milliondollars100percentrealnofake.com");
        mockPost.setTags("dollar,1 million,earndollars");

        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = new PostPatchEditRequest();
        request.setSubtype(subId);
        request.setDescription("Soy una descripcion");
        request.setTags("descripcion,breve");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            postService.update(id, request, mockUser);
        });

        assertEquals("Subtype not found", exception.getMessage());
    }

    @Test
    @DisplayName("Modificar post por ID, barrio inexistente.")
    void updatePost_NeighbourhoodNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        long neighId = 4;

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(neighId)).thenReturn(Optional.empty());

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        mockPost.setTitle("Siga los pasos de esta publicación y gane 1 millón de dólares");
        mockPost.setSubtype(new PostSubtype());
        mockPost.setDescription("Haga click aquí: www.1milliondollars100percentrealnofake.com");
        mockPost.setNeighbourhood(neighbourhood);
        mockPost.setTags("dollar,1 million,earndollars");

        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = new PostPatchEditRequest();
        request.setDescription("Soy una descripcion");
        request.setNeighbourhood(neighId);
        request.setTags("descripcion,breve");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            postService.update(id, request, mockUser);
        });

        assertEquals("Neighbourhood not found", exception.getMessage());
    }

    @Test
    @DisplayName("Modificar post por ID, permisos insuficientes.")
    void updatePost_AccessDenied_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        String username2 = "ronald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        User mockUser2 = new User();
        mockUser2.setUsername(username2);
        mockUser2.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        mockPost.setTitle("Siga los pasos de esta publicación y gane 1 millón de dólares");
        mockPost.setSubtype(new PostSubtype());
        mockPost.setDescription("Haga click aquí: www.1milliondollars100percentrealnofake.com");
        mockPost.setTags("dollar,1 million,earndollars");

        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        var request = new PostPatchEditRequest();
        request.setDescription("Soy una descripcion");
        request.setTags("descripcion,breve");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            postService.update(id, request, mockUser2);
        });

        assertEquals("Insufficient permissions", exception.getMessage());
    }

    @Test
    @DisplayName("Eliminar post por ID.")
    void deletePost_PostFound_ReturnsNoContent() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        assertDoesNotThrow(() -> {
            postService.delete(id, mockUser);
        });
    }


    @Test
    @DisplayName("Eliminar post por ID, ID inexistente.")
    void deletePost_PostNotFound_ThrowsException() {
        Integer id = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.delete(id, mockUser);
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("Eliminar post por ID, permisos insuficientes.")
    void deletePost_AccessDenied_ThrowsException() {
        Integer id = 1;
        String username = "donald";
        String username2 = "ronald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        User mockUser2 = new User();
        mockUser2.setUsername(username2);
        mockUser2.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Post mockPost = new Post();
        mockPost.setId(id);
        mockPost.setOwner(mockUser);
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            postService.delete(id, mockUser2);
        });

        assertEquals("Insufficient permissions", exception.getMessage());
    }

}