package frgp.utn.edu.ar.quepasa.service.media;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Document;
import frgp.utn.edu.ar.quepasa.repository.media.DocumentRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.media.impl.DocumentServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.MultipartFileValidator;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static frgp.utn.edu.ar.quepasa.service.validators.MultipartFileValidator.MB;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Servicio de documentos")
public class DocumentServiceTests {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private OwnerService ownerService;

    private User mockUser;
    private Document mockDocument;
    private UUID docId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1790);
        mockUser.setUsername("testuser");

        mockDocument = new Document();
        mockDocument.setId(docId);
        mockDocument.setDescription("Test description");
        mockDocument.setOwner(mockUser);
    }

    @Test
    @DisplayName("#56: Subir documento")
    void testUploadSuccessful() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(docId);
            return doc;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getContentType()).thenReturn("application/pdf");
        when(validator.build()).thenReturn(file);

        Document uploadedDocument = documentService.upload(file, "Test description");

        assertNotNull(uploadedDocument);
        assertEquals("Test description", uploadedDocument.getDescription());
        assertEquals(mockUser, uploadedDocument.getOwner());
        assertTrue(uploadedDocument.isActive());
        verify(documentRepository, times(2)).save(any(Document.class));
        verify(storageService).store(file, "document." + uploadedDocument.getId().toString());

    }

    @Test
    @DisplayName("#57: Obtener documentos del usuario autenticado")
    void testGetCurrentUserDocs() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(documentRepository.findByOwner(any(User.class), any(Pageable.class))).thenAnswer(invocation -> {
            Document a = new Document();
            a.setId(docId);
            a.setDescription("Test description");
            a.setOwner(mockUser);
            Document b = new Document();
            b.setId(UUID.randomUUID());
            b.setDescription("Test description");
            b.setOwner(mockUser);
            return new PageImpl<>(List.of(a, b), invocation.getArgument(1), 2);
        });

        var page = documentService.getMyDocuments(Pageable.ofSize(5));

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertEquals(5, page.getSize());
        assertEquals(2, page.getTotalElements());
        assertTrue(page.stream().findFirst().isPresent());
        assertEquals("Test description", page.stream().findFirst().get().getDescription());
        assertEquals(docId, page.stream().findFirst().get().getId());
        verify(documentRepository).findByOwner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("#56: Subir documento, archivo vacío")
    void testUploadEmptyFile() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(docId);
            return doc;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.isEmpty()).thenReturn(true);
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> documentService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#56: Subir documento, sin tipo de archivo")
    void testUploadNullContentType() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(docId);
            return doc;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getContentType()).thenReturn(null);
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> documentService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#56: Subir documento, tipo de archivo inválido")
    void testUploadForbiddenContentType() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(docId);
            return doc;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getContentType()).thenReturn("text/html");
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> documentService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#56: Subir documento, tamaño mayor al máximo")
    void testUploadFileTooHeavy() {
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(docId);
            return doc;
        });

        MultipartFileValidator validator = mock(MultipartFileValidator.class);
        when(file.getSize()).thenReturn(100 * MB);
        when(validator.build()).thenReturn(file);

        var expected = assertThrows(ValidationError.class, () -> documentService.upload(file, "Test description"));
        assertEquals(expected.getField(), "file");
        assertFalse(expected.getErrors().isEmpty());

    }

    @Test
    @DisplayName("#58: Buscar documento por ID, no encontrado")
    void testGetRawDocumentByIdNotFound() {
        UUID documentId = UUID.randomUUID();
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> {
            documentService.getRawDocumentById(documentId);
        });

        assertEquals("Document not found. ", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("#60: Eliminar documento no existente")
    void testDeleteDocumentNotFound() {
        var documentId = UUID.randomUUID();
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> documentService.delete(documentId));

        assert exception.getMessage().contains("Document not found");
        assert exception.getStatus() == HttpStatus.NOT_FOUND;

        verify(documentRepository, times(1)).findById(documentId);
        verify(documentRepository, never()).delete(any(Document.class));
        verify(storageService, never()).delete(anyString());
    }

    @Test
    @DisplayName("#60: Eliminar documento")
    void testDeleteDocumentSuccess() {
        var documentId = UUID.randomUUID();
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(mockDocument));

        OwnerValidator vb = Mockito.mock(OwnerValidator.class);
        when(vb.isOwner()).thenReturn(vb);
        when(vb.isAdmin()).thenReturn(vb);
        when(ownerService.of(any(Ownable.class))).thenReturn(vb);

        doNothing().when(storageService).delete("document." + documentId);

        documentService.delete(documentId);

        verify(documentRepository, times(1)).findById(documentId);
        verify(storageService, times(1)).delete(anyString());
        verify(documentRepository, times(1)).delete(mockDocument);
    }

}
