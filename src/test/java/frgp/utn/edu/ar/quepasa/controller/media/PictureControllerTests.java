package frgp.utn.edu.ar.quepasa.controller.media;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.media.StorageFileNotFoundException;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import frgp.utn.edu.ar.quepasa.service.media.impl.PictureServiceImpl;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DisplayName("Controlador de imágenes")
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "root", roles = "ADMIN")
public class PictureControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PictureServiceImpl pictureService;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @MockBean
    private StorageService storageService;

    private Picture mockPic;
    private UUID pictureId;

    public PictureControllerTests() {
        pictureId = UUID.randomUUID();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User root = new User();
        root.setUsername("root");
        root.setRole(Role.ADMIN);
        root.setId(10009);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(root);
        mockPic = new Picture();
        mockPic.setId(pictureId);
        mockPic.setDescription("Test description");
    }

    @Test
    @DisplayName("#61: Subida de imágenes exitosa")
    void testUploadPicture() throws Exception {
        var picId = UUID.randomUUID();
        AtomicReference<UUID> id = new AtomicReference<>(picId);
        when(pictureService.upload(any(), any())).thenAnswer(_ -> {
            Picture pic = new Picture();
            pic.setId(picId);
            id.set(pic.getId());
            pic.setDescription("Test description");
            return pic;
        });

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_JPEG_VALUE,
                "Test File Content".getBytes()
        );

        mockMvc.perform(
                multipart("/api/pictures")
                        .file(mockFile)
                        .param("description", "Test description")
                        .with(user("root").password("123456789").roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.id").value(picId.toString()));
    }

    @Test
    @DisplayName("#64: Test obtener imagen por ID")
    void testGetPictureById() throws Exception {
        when(pictureService.getPictureById(any(UUID.class)))
                .thenAnswer(invocation -> {
                    Picture pic = invocation.getArgument(0);
                    pic.setId(pictureId);
                    pic.setDescription(mockPic.getDescription());
                    return pic;
                });

        mockMvc.perform(get("/api/pictures/" + pictureId.toString())
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("#62: Listar mis imágenes")
    void testGetMyPics() throws Exception {
        UUID picId = UUID.randomUUID();
        when(pictureService.getMyPics(any(Pageable.class))).thenAnswer(invocation -> {
            Picture a = new Picture();
            a.setId(picId);
            a.setDescription("Test description");
            Picture b = new Picture();
            b.setId(UUID.randomUUID());
            b.setDescription("Test description");
            return new PageImpl<>(List.of(a, b), invocation.getArgument(0), 2);
        });

        mockMvc.perform(get("/api/pictures")
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(picId.toString()))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("#63: Ver imagen JPEG")
    void testViewPicture() throws Exception {
        byte[] content = "Picture content".getBytes();
        Picture pic = new Picture();
        pic.setId(pictureId);
        pic.setDescription(mockPic.getDescription());
        Resource mr = new ByteArrayResource(content);
        var rd = new RawPicture(pic, mr);
        when(storageService.loadAsResource(anyString())).thenAnswer(_ -> mr);
        when(pictureService.getRawPictureById(any(String.class)))
                .thenAnswer(_ -> rd);
        when(pictureService.getRawPictureById(any(UUID.class)))
                .thenAnswer(_ -> rd);
        // when(pictureRepository.findById(any(UUID.class))).thenReturn(Optional.of(pic));


        mockMvc.perform(get("/api/pictures/{id}/view", pictureId.toString())
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + pictureId.toString() + "\""))
                .andExpect(content().bytes("Picture content".getBytes()));
    }

    @Test
    @DisplayName("#63: Test manejo de excepciones para imagen no encontrada")
    void testHandleStorageFileNotFound() throws Exception {
        when(pictureService.getRawPictureById(any(String.class)))
                .thenThrow(new StorageFileNotFoundException("File not found"));

        mockMvc.perform(get("/api/pictures/{id}/view", pictureId.toString())
                        .with(user("root").password("123456789").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("#66: Eliminar imagen exitosamente")
    void testDeletePictureSuccess() throws Exception {
        mockMvc.perform(delete("/api/pictures/{id}", pictureId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("root").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(pictureService).delete(pictureId);
    }

    @Test
    @DisplayName("#66: Eliminar imagen no existente")
    void testDeletePictureNotFound() throws Exception {
        doThrow(new Fail("Picture not found.", HttpStatus.NOT_FOUND))
                .when(pictureService).delete(pictureId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pictures/{id}", pictureId)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("root").roles("ADMIN")))
                .andExpect(status().isNotFound());

        verify(pictureService).delete(pictureId);
    }

}
