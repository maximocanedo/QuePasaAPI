package frgp.utn.edu.ar.quepasa.service.media;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.impl.VoteServiceImpl;
import frgp.utn.edu.ar.quepasa.service.media.impl.PictureServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidator;
import frgp.utn.edu.ar.quepasa.service.validators.objects.MultipartFileValidator;
import static frgp.utn.edu.ar.quepasa.service.validators.objects.MultipartFileValidator.MB;
import quepasa.api.exceptions.ValidationError;

@DisplayName("Servicio de imágenes")
public class PictureServiceTests {

    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private PictureServiceImpl pictureService;

    @Mock
    private OwnerService ownerService;

    @Mock
    private VoteServiceImpl voteService;

    private User mockUser;
    private Picture mockPic;
    private UUID picId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1790);
        mockUser.setUsername("testuser");

        mockPic = new Picture();
        mockPic.setId(picId);
        mockPic.setDescription("Test description");
        mockPic.setOwner(mockUser);


    }

    @Test
    @DisplayName("#61: Subir imagen")
    void testUploadSuccessful() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> {
            Picture pic = invocation.getArgument(0);
            pic.setId(picId);
            return pic;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(validator.build()).thenReturn(file);

        Picture uploadedPicture = pictureService.upload(file, "Test description");

        assertNotNull(uploadedPicture);
        assertEquals("Test description", uploadedPicture.getDescription());
        assertEquals(mockUser, uploadedPicture.getOwner());
        assertTrue(uploadedPicture.isActive());
        verify(pictureRepository, times(2)).save(any(Picture.class));
        verify(storageService).store(file, "picture." + uploadedPicture.getId().toString());

    }

    @Test
    @DisplayName("#62: Obtener imágenes del usuario autenticado")
    void testGetCurrentUserPics() {
        when(voteService.populate(any(Picture.class))).thenAnswer(invocation -> {
            Picture inv = invocation.getArgument(0);
            var c = new VoteCount();
            c.setVotes(129);
            c.setUservote(1);
            c.setUpdated(new Timestamp(System.currentTimeMillis()));
            inv.setVotes(c);
            return inv;
        });
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(pictureRepository.findByOwner(any(User.class), any(Pageable.class))).thenAnswer(invocation -> {
            Picture a = new Picture();
            a.setId(picId);
            a.setDescription("Test description");
            a.setOwner(mockUser);
            Picture b = new Picture();
            b.setId(UUID.randomUUID());
            b.setDescription("Test description");
            b.setOwner(mockUser);
            return new PageImpl<>(List.of(a, b), invocation.getArgument(1), 2);
        });

        var page = pictureService.getMyPics(Pageable.ofSize(5));

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertEquals(5, page.getSize());
        assertEquals(2, page.getTotalElements());
        assertTrue(page.stream().findFirst().isPresent());
        assertEquals("Test description", page.stream().findFirst().get().getDescription());
        assertEquals(picId, page.stream().findFirst().get().getId());
        verify(pictureRepository).findByOwner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("#61: Subir imagen, archivo vacío")
    void testUploadEmptyFile() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> {
            Picture pic = invocation.getArgument(0);
            pic.setId(picId);
            return pic;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.isEmpty()).thenReturn(true);
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> pictureService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#61: Subir imagen, sin tipo de archivo")
    void testUploadNullContentType() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> {
            Picture pic = invocation.getArgument(0);
            pic.setId(picId);
            return pic;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getContentType()).thenReturn(null);
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> pictureService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#61: Subir imagen, tipo de archivo inválido")
    void testUploadForbiddenContentType() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> {
            Picture pic = invocation.getArgument(0);
            pic.setId(picId);
            return pic;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getContentType()).thenReturn("text/html");
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> pictureService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#61: Subir imagen, tamaño mayor al máximo")
    void testUploadFileTooHeavy() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> {
            Picture pic = invocation.getArgument(0);
            pic.setId(picId);
            return pic;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getSize()).thenReturn(100 * MB);
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> pictureService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#63: Buscar imagen por ID, no encontrado")
    void testGetRawPictureByIdNotFound() {
        UUID pictureId = UUID.randomUUID();
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> {
            pictureService.getRawPictureById(pictureId);
        });

        assertEquals("Picture not found. ", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("#66: Eliminar imagen no existente")
    void testDeletePictureNotFound() {
        var pictureId = UUID.randomUUID();
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> pictureService.delete(pictureId));

        assert exception.getMessage().contains("Picture not found");
        assert exception.getStatus() == HttpStatus.NOT_FOUND;

        verify(pictureRepository, times(1)).findById(pictureId);
        verify(pictureRepository, never()).delete(any(Picture.class));
        verify(storageService, never()).delete(anyString());

    }

    @Test
    @DisplayName("#66: Eliminar imagen")
    void testDeletePictureSuccess() {
        var pictureId = UUID.randomUUID();
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(mockPic));

        OwnerValidator vb = Mockito.mock(OwnerValidator.class);
        when(vb.isOwner()).thenReturn(vb);
        when(vb.isAdmin()).thenReturn(vb);
        when(ownerService.of(any(Ownable.class))).thenReturn(vb);

        doNothing().when(storageService).delete("picture." + pictureId);

        pictureService.delete(pictureId);

        verify(pictureRepository, times(1)).findById(pictureId);
        verify(storageService, times(1)).delete(anyString());
        verify(pictureRepository, times(1)).delete(mockPic);
    }

}
