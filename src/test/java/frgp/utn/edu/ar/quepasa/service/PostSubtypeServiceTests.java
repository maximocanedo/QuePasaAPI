package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Servicio de subtipos de publicaciones")
public class PostSubtypeServiceTests {

    private PostSubtypeRepository postSubtypeRepository;
    private PostTypeRepository postTypeRepository;
    private PostSubtypeServiceImpl postSubtypeService;

    @BeforeEach
    public void setUp() {
        this.postSubtypeRepository = Mockito.mock(PostSubtypeRepository.class);
        this.postTypeRepository = Mockito.mock(PostTypeRepository.class);

        this.postSubtypeService = new PostSubtypeServiceImpl(postSubtypeRepository, postTypeRepository);
    }

    @Test
    @DisplayName("Buscar subtipo de post por ID.")
    public void findById_SubtypeFound_ReturnsSubtype() {
        Integer id = 1;
        PostSubtype mockSubtype = new PostSubtype();
        mockSubtype.setId(id);


        when(postSubtypeRepository.findActiveById(id)).thenReturn(Optional.of(mockSubtype));

        PostSubtype foundSubtype = postSubtypeService.findById(id);
        assertNotNull(foundSubtype);
        assertEquals(id, foundSubtype.getId());
    }

    @Test
    @DisplayName("Buscar subtipo de post por ID, ID inexistente.")
    public void findById_SubtypeNotFound_ThrowsException() {
        Integer id = 1;

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postSubtypeService.findById(id);
        });

        assertEquals("Subtype not found", exception.getMessage());
    }

    @Test
    @DisplayName("Buscar subtipos de post.")
    public void findSubtypes_SubtypesFound_ReturnsSubtypes() {
        Pageable pageable = PageRequest.of(0, 10);

        PostSubtype subtype = new PostSubtype();
        Page<PostSubtype> subtypePage = new PageImpl<>(List.of(subtype));

        when(postSubtypeRepository.findAllActive(pageable)).thenReturn(subtypePage);

        Page<PostSubtype> subtypes = postSubtypeService.findAll(pageable, true);

        assertNotNull(subtypes);
        assertFalse(subtypes.isEmpty());
    }

    @Test
    @DisplayName("Buscar subtipos de post por tipo.")
    public void findByType_TypeFound_ReturnsSubtypes() {
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
    public void findByType_TypeNotFound_ThrowsException() {
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
    public void createSubtype_SubtypeNew_ReturnsSubtype() {
        Integer idType = 1;

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findActiveById(idType)).thenReturn(Optional.of(mockType));

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        AtomicReference<PostSubtype> createdSubtype = new AtomicReference<>();

        assertDoesNotThrow(() -> {
           createdSubtype.set(postSubtypeService.create(request));
        });
        assertNotNull(createdSubtype.get());
        assertEquals(idType, createdSubtype.get().getType().getId());
    }

    @Test
    @DisplayName("Crear subtipo de post. Tipo inexistente.")
    public void createSubtype_SubtypeNotFound_ThrowsException() {
        int idType = 1;

        when(postTypeRepository.findActiveById(idType)).thenReturn(Optional.empty());

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(1);
        request.setDescription("Entretenimiento");

        Fail exception = assertThrows(Fail.class, () -> {
            postSubtypeService.create(request);
        });

        assertEquals("Type not found. ", exception.getMessage());
    }

    @Test
    @DisplayName("Modificar subtipo de post por ID.")
    public void updateSubtype_SubtypeFound_GoodData() {
        Integer id = 1;
        Integer idType = 4;

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findActiveById(idType)).thenReturn(Optional.of(mockType));

        PostSubtype mockSubtype = new PostSubtype();
        mockSubtype.setId(id);

        when(postSubtypeRepository.findActiveById(id)).thenReturn(Optional.of(mockSubtype));

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(idType);
        request.setDescription("Entretenimiento");

        AtomicReference<PostSubtype> createdSubtype = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            createdSubtype.set(postSubtypeService.update(id, request));
        });
        assertNotNull(createdSubtype.get());
        assertEquals(request.getDescription(), createdSubtype.get().getDescription());
    }

    @Test
    @DisplayName("Modificar subtipo de post por ID, de un subtipo inexistente.")
    public void updateSubtype_SubtypeNotFound_ThrowsException() {
        Integer id = 1;
        Integer idType = 4;

        PostType mockType = new PostType();
        mockType.setId(idType);

        when(postTypeRepository.findById(idType)).thenReturn(Optional.of(mockType));

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.empty());

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(idType);
        request.setDescription("Entretenimiento");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postSubtypeService.update(id, request);
        });

        assertEquals("Subtype not found", exception.getMessage());
    }

    @Test
    @DisplayName("Modificar subtipo de post por ID, tipo inexistente.")
    public void updateSubtype_TypeNotFound_ThrowsException() {
        Integer id = 1;
        Integer idType = 4;

        when(postTypeRepository.findActiveById(idType)).thenReturn(Optional.empty());

        PostSubtype mockSubtype = new PostSubtype();
        mockSubtype.setId(id);

        when(postSubtypeRepository.findActiveById(id)).thenReturn(Optional.of(mockSubtype));

        PostSubtypeRequest request = new PostSubtypeRequest();
        request.setType(idType);
        request.setDescription("Entretenimiento");

        Fail exception = assertThrows(Fail.class, () -> {
            postSubtypeService.update(id, request);
        });

        assertEquals("Type not found. ", exception.getMessage());
    }

    @Test
    @DisplayName("Eliminar subtipo por ID.")
    public void deleteSubtype_SubtypeFound_ReturnsNoContent() {
        Integer id = 1;

        PostSubtype mockSubtype = new PostSubtype();
        mockSubtype.setId(id);

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.of(mockSubtype));

        assertDoesNotThrow(() -> {
            postSubtypeService.delete(id);
        });
    }

    @Test
    @DisplayName("Eliminar subtipo por ID, ID inexistente.")
    public void deleteSubtype_SubtypeNotFound_ThrowsException() {
        Integer id = 1;

        when(postSubtypeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postSubtypeService.delete(id);
        });

        assertEquals("Subtype not found", exception.getMessage());
    }

}
