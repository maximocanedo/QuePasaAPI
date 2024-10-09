package frgp.utn.edu.ar.quepasa.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.controller.UserController;
import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.fakedata.NapoleonBonaparteInspiredData;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserUpdateActionControllerTests {

    @MockBean private UserRepository userRepository;
    @MockBean private NeighbourhoodRepository neighbourhoodRepository;
    @MockBean private PictureRepository pictureRepository;
    @MockBean private AuthenticationService authenticationService;
    private NapoleonBonaparteInspiredData data = new NapoleonBonaparteInspiredData();
    @Autowired ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        User napoleon = data.napoleonBonaparte();
        when(userRepository.findByUsername(napoleon.getUsername())).thenReturn(Optional.of(napoleon));
        when(userRepository.findByUsername(data.mariaLuisaDeAustria().getUsername())).thenReturn(Optional.of(data.mariaLuisaDeAustria()));
       // when(userService.loadUserByUsername(napoleon.getUsername())).thenReturn(Optional.of(napoleon));
        when(neighbourhoodRepository.findById(data.longwood().getId())).thenReturn(Optional.of(data.longwood()));
        when(neighbourhoodRepository.findById(data.villaDeiMulini().getId())).thenReturn(Optional.of(data.villaDeiMulini()));
        when(neighbourhoodRepository.findActiveNeighbourhoodById(data.longwood().getId())).thenReturn(Optional.of(data.longwood()));
        when(neighbourhoodRepository.findActiveNeighbourhoodById(data.villaDeiMulini().getId())).thenReturn(Optional.of(data.villaDeiMulini()));

        when(pictureRepository.findById(data.napoleonCruzandoLosAlpes().getId())).thenReturn(Optional.of(data.napoleonCruzandoLosAlpes()));
        when(pictureRepository.findById(data.autorretrato().getId())).thenReturn(Optional.of(data.autorretrato()));
        when(pictureRepository.findById(data.autorretratoDeOtraPersona().getId())).thenReturn(Optional.of(data.autorretratoDeOtraPersona()));

        when(authenticationService.getCurrentUserOrDie()).thenReturn(napoleon);
        given(authenticationService.getCurrentUser()).willReturn(Optional.of(napoleon));

        assertNotNull(mockMvc);
        assertNotNull(authenticationService);
        setUpSecurityContext(napoleon);
    }

    private void setUpSecurityContext(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @WithMockUser(username = "napoleon.bonaparte", roles = {"USER"})
    @DisplayName("Modificar usuario: Solicitud completa, autenticado")
    public void fullRequest_Authenticated() throws Exception {
        given(userRepository.findByUsername(ArgumentMatchers.anyString())).willReturn(Optional.of(data.napoleonBonaparte()));
        var request = fullRequestFile();
        var response = mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJsonString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(data.napoleonBonaparte().getUsername()))
                .andExpect(jsonPath("$.nonExistent.path").value(data.napoleonBonaparte().getUsername()))
                .andReturn();
        assertNotNull(response);
        assertFalse(response.getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud parcial, autenticado")
    public void partialRequest_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Usuario inactivo, autenticado")
    public void inactiveUser_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Usuario no existente, autenticado")
    public void userNotFound_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Nombre incorrecto, autenticado")
    public void invalidName_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Barrio no encontrado, autenticado")
    public void neighbourhoodNotFound_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Barrio inactivo, autenticado")
    public void inactiveNeighbourhood_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Imagen inactiva, autenticado")
    public void inactiveImage_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Imagen no encontrada, autenticado")
    public void imageNotFound_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Sin derechos sobre la imagen, autenticado")
    public void noUseRightsOverImage_Authenticated() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario administrador. ")
    public void fullRequest() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario moderador. ")
    public void fullRequestAsModerator() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario entidad gubernamental. ")
    public void fullRequestAsGovt() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario organización. ")
    public void fullRequestAsOrganization() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario contribuidor. ")
    public void fullRequestAsContributor() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario vecino. ")
    public void fullRequestAsNeighbour() throws Exception {

    }


    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario sin verificar. ")
    public void fullRequestAsUser() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Solicitud parcial, autenticado")
    public void partialRequest() throws Exception {

    }


    @Test
    @DisplayName("Modificar usuario: Usuario inactivo")
    public void inactiveUser() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Usuario no existente")
    public void userNotFound() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Nombre incorrecto")
    public void invalidName() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Barrio no encontrado")
    public void neighbourhoodNotFound() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Barrio inactivo")
    public void inactiveNeighbourhood() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Imagen inactiva")
    public void inactiveImage() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Imagen no encontrada")
    public void imageNotFound() throws Exception {

    }

    @Test
    @DisplayName("Modificar usuario: Sin derechos sobre la imagen")
    public void noUseRightsOverImage() throws Exception {

    }



    public UserPatchEditRequest fullRequestFile() {
        var pic = data.autorretrato();
        pic.setOwner(null);
        var request = new UserPatchEditRequest();
        request.setName("Napoleón Bonaparte");
        request.setNeighbourhood(data.longwood());
        request.setAddress("Avocado st. 1533");
        request.setPicture(pic);
        return request;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
