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
import org.mockito.Mockito;
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

@DisplayName("Servicio de tipos de publicaciones")
public class PostTypeServiceTests {

    private PostTypeRepository postTypeRepository;
    private PostTypeServiceImpl postTypeService;

    @BeforeEach
    void setUp() {
        this.postTypeRepository = Mockito.mock(PostTypeRepository.class);

        this.postTypeService = new PostTypeServiceImpl(postTypeRepository);
    }

    @Test
    @DisplayName("Buscar tipo de post por ID.")
    void findById_TypeFound_ReturnsType() {
        Integer id = 1;
        PostType mockType = new PostType();
        mockType.setId(id);

        when(postTypeRepository.findById(id)).thenReturn(Optional.of(mockType));

        PostType foundType = postTypeService.findById(id);
        assertNotNull(foundType);
        assertEquals(id, foundType.getId());
    }

    @Test
    @DisplayName("Buscar tipo de post por ID, ID inexistente.")
    void findById_TypeNotFound_ThrowsException() {
        Integer id = 1;

        when(postTypeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTypeService.findById(id);
        });

        assertEquals("Type not found", exception.getMessage());
    }

    @Test
    @DisplayName("Buscar tipos de post.")
    void findTypes_TypesFound_ReturnsTypes() {
        Pageable pageable = PageRequest.of(0, 10);

        PostType type = new PostType();
        Page<PostType> typePage = new PageImpl<>(List.of(type));

        when(postTypeRepository.findAll(pageable)).thenReturn(typePage);

        Page<PostType> types = postTypeRepository.findAll(pageable);

        assertNotNull(types);
        assertFalse(types.isEmpty());
    }

    @Test
    @DisplayName("Crear tipo de post.")
    void createType_TypeNew_ReturnsType() {
        String description = "Recreativo";

        AtomicReference<PostType> createdPost = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            createdPost.set(postTypeService.create(description));
        });
        assertNotNull(createdPost.get());
        assertEquals(description, createdPost.get().getDescription());
    }

    @Test
    @DisplayName("Modificar tipo de post por ID.")
    void updateType_TypeFound_GoodData() {
        Integer id = 1;
        String description = "Recreativo";

        PostType mockType = new PostType();
        mockType.setDescription(description);

        when(postTypeRepository.findById(id)).thenReturn(Optional.of(mockType));

        AtomicReference<PostType> createdPost = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            createdPost.set(postTypeService.update(1, description));
        });
        assertNotNull(createdPost.get());
        assertEquals(description, createdPost.get().getDescription());
    }

    @Test
    @DisplayName("Modificar tipo de post por ID, de un tipo no existente.")
    void updateType_TypeNotFound_ThrowsException() {
        Integer id = 1;
        String description = "Recreativo";

        when(postTypeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTypeService.update(id, description);
        });

        assertEquals("Type not found", exception.getMessage());
    }

    @Test
    @DisplayName("Eliminar tipo de post por ID.")
    void deleteType_TypeFound_ReturnsNoContent() {
        Integer id = 1;

        PostType mockType = new PostType();
        mockType.setId(1);

        when(postTypeRepository.findById(id)).thenReturn(Optional.of(mockType));

        assertDoesNotThrow(() -> {
            postTypeService.delete(id);
        });
    }

    @Test
    @DisplayName("Eliminar tipo de post por ID, ID inexistente.")
    void deleteType_TypeNotFound_ThrowsException() {
        Integer id = 1;

        when(postTypeRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postTypeService.delete(id);
        });

        assertEquals("Type not found", exception.getMessage());
    }

}
