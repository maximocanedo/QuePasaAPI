package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.PostSubtypeServiceImpl;
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

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PostSubtypeServiceTests {

    @Mock private PostSubtypeRepository postSubtypeRepository;
    @Mock private PostTypeRepository postTypeRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private PostSubtypeServiceImpl postSubtypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Buscar subtipo de post por ID.")
    void findById_SubtypeFound_ReturnsSubtype() {
        Integer id = 1;
        PostSubtype mockSubtype = new PostSubtype();
        mockSubtype.setId(id);

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.of(mockSubtype));

        PostSubtype foundSubtype = postSubtypeService.findById(id);
        assertNotNull(foundSubtype);
        assertEquals(id, foundSubtype.getId());
    }

    @Test
    @DisplayName("Buscar subtipo de post por ID, ID inexistente.")
    void findById_SubtypeNotFound_ThrowsException() {
        Integer id = 1;

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postSubtypeService.findById(id);
        });

        assertEquals("Subtype not found", exception.getMessage());
    }

    @Test
    @DisplayName("Buscar subtipos de post.")
    void findSubtypes_SubtypesFound_ReturnsSubtypes() {
        Pageable pageable = PageRequest.of(0, 10);

        PostSubtype subtype = new PostSubtype();
        Page<PostSubtype> subtypePage = new PageImpl<>(List.of(subtype));

        when(postSubtypeRepository.findAll(pageable)).thenReturn(subtypePage);

        Page<PostSubtype> subtypes = postSubtypeService.listPostSubtypes(pageable);

        assertNotNull(subtypes);
        assertFalse(subtypes.isEmpty());
    }

    @Test
    @DisplayName("Buscar subtipos de post por tipo.")
    void findByType_TypeFound_ReturnsSubtypes() {
        Integer id = 1;
        Integer idType = 4;
        Pageable pageable = PageRequest.of(0, 10);
        PostSubtype mockSubtype = new PostSubtype();
        mockSubtype.setId(id);

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.of(mockSubtype));

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findById(idType)).thenReturn(Optional.of(mockType));

        mockSubtype.setType(mockType);
        Page<PostSubtype> subtypePage = new PageImpl<>(List.of(mockSubtype));

        when(postSubtypeRepository.findByType(mockType, pageable)).thenReturn(subtypePage);

        Page<PostSubtype> subtypes = postSubtypeService.findByType(idType, pageable);
        assertNotNull(subtypes);
        assertFalse(subtypes.isEmpty());
    }

    @Test
    @DisplayName("Buscar subtipos de post por tipo, tipo no existente.")
    void findByType_TypeNotFound_ThrowsException() {
        Integer idType = 4;
        Pageable pageable = PageRequest.of(0, 10);

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findById(idType)).thenReturn(Optional.empty());

        when(postSubtypeRepository.findByType(mockType, pageable)).thenReturn(Page.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postSubtypeService.findByType(idType, pageable);
        });
    }

    @Test
    @DisplayName("Crear subtipo de post.")
    void createSubtype_SubtypeNew_ReturnsSubtype() {
        Integer idType = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findById(idType)).thenReturn(Optional.of(mockType));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        AtomicReference<PostSubtype> createdSubtype = new AtomicReference<>();

        assertDoesNotThrow(() -> {
           createdSubtype.set(postSubtypeService.create(request, mockUser));
        });
        assertNotNull(createdSubtype.get());
        assertEquals(idType, createdSubtype.get().getType().getId());
    }

    @Test
    @DisplayName("Crear subtipo de post. Tipo inexistente.")
    void createSubtype_SubtypeNotFound_ThrowsException() {
        Integer idType = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.ADMIN);

        when(postTypeRepository.findById(idType)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postSubtypeService.create(request, mockUser);
        });

        assertEquals("Type not found", exception.getMessage());
    }

    @Test
    @DisplayName("Crear subtipo de post. Permisos insuficientes.")
    void createSubtype_AccessDenied_ThrowsException() {
        Integer idType = 1;
        String username = "donald";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole(Role.USER);

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findById(idType)).thenReturn(Optional.of(mockType));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            postSubtypeService.create(request, mockUser);
        });

        assertEquals("Insufficient permissions", exception.getMessage());
    }
}
