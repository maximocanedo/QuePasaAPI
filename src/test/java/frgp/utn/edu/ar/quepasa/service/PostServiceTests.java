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
        assertEquals(username, createdPost.get().getOriginalPoster().getUsername());
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

}