package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.PostTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PostTypeServiceTests {

    @Mock private PostTypeRepository postTypeRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private PostTypeServiceImpl postTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Crear tipo de post.")
    void createType_TypeNew_ReturnsType() {
        String description = "Recreativo";
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        PostType mockType = new PostType();
        mockType.setDescription(description);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        AtomicReference<PostType> createdPost = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            createdPost.set(postTypeService.create(description, mockUser));
        });
        assertNotNull(createdPost.get());
        assertEquals(description, createdPost.get().getDescription());
    }

    @Test
    @DisplayName("Crear tipo de post. Permisos insuficientes.")
    void createType_AccessDenied_ThrowsException() {
        String description = "Recreativo";
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        PostType mockType = new PostType();
        mockType.setDescription(description);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            postTypeService.create(description, mockUser);
        });

        assertEquals("Insufficient permissions", exception.getMessage());
    }

}
