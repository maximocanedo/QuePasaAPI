package frgp.utn.edu.ar.quepasa.controller.media;

import frgp.utn.edu.ar.quepasa.data.response.RawDocument;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.media.Document;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.media.StorageFileNotFoundException;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import frgp.utn.edu.ar.quepasa.service.media.impl.DocumentServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "root", roles = "ADMIN")
@DisplayName("Controlador de documentos")
public class DocumentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentServiceImpl documentService;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private StorageService storageService;


    private Document mockDocument;
    private UUID documentId;

    public DocumentControllerTests() {

        documentId = UUID.randomUUID();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User root = new User();
        root.setUsername("root");
        root.setRole(Role.ADMIN);
        root.setId(10009);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(root);
        mockDocument = new Document();
        mockDocument.setId(documentId);
        mockDocument.setDescription("Test description");
    }

    @Test
    @DisplayName("#56: Subida de documentos exitosa")
    void testUploadDocument() throws Exception {
        var docId = UUID.randomUUID();
        AtomicReference<UUID> id = new AtomicReference<>(docId);
        when(documentService.upload(any(), any())).thenAnswer(_ -> {
            Document doc = new Document();
            doc.setId(docId);
            id.set(doc.getId());
            doc.setDescription("Test description");
            return doc;
        });

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test File Content".getBytes()
        );

        mockMvc.perform(
                multipart("/api/documents")
                        .file(mockFile)
                        .param("description", "Test description")
                        .with(user("root").password("123456789").roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.id").value(docId.toString()));
    }

    @Test
    @DisplayName("#58: Test obtener documento por ID")
    void testGetDocumentById() throws Exception {
        when(documentService.getDocumentById(any(UUID.class)))
                .thenAnswer(invocation -> {
                    Document doc = invocation.getArgument(0);
                    doc.setId(documentId);
                    doc.setDescription(mockDocument.getDescription());
                    return doc;
                });

        mockMvc.perform(get("/api/documents/" + documentId.toString())
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test visualizar documento como recurso PDF")
    void testViewDocument() throws Exception {
        byte[] content = "PDF content".getBytes();
        Document doc = new Document();
        doc.setId(documentId);
        doc.setDescription(mockDocument.getDescription());
        Resource mr = new ByteArrayResource(content);
        var rd = new RawDocument(doc, mr);
        when(storageService.loadAsResource(anyString())).thenAnswer(_ -> mr);
        when(documentService.getRawDocumentById(any(String.class)))
                .thenAnswer(_ -> rd);
        when(documentService.getRawDocumentById(any(UUID.class)))
                .thenAnswer(_ -> rd);
        // when(documentRepository.findById(any(UUID.class))).thenReturn(Optional.of(doc));


        mockMvc.perform(get("/api/documents/{id}/view", documentId.toString())
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + documentId.toString() + "\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes("PDF content".getBytes()));
    }

    @Test
    @DisplayName("#58: Test manejo de excepciones para documento no encontrado")
    void testHandleStorageFileNotFound() throws Exception {
        when(documentService.getRawDocumentById(any(String.class)))
                .thenThrow(new StorageFileNotFoundException("File not found"));

        mockMvc.perform(get("/api/documents/{id}/view", documentId.toString())
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

}
