package frgp.utn.edu.ar.quepasa.controller.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.fakedata.NapoleonBonaparteInspiredData;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserUpdateActionControllerTests {

    @MockBean private UserRepository userRepository;
    @MockBean private NeighbourhoodRepository neighbourhoodRepository;
    @MockBean private PictureRepository pictureRepository;

    @MockBean(answer = Answers.RETURNS_MOCKS) private AuthenticationService authenticationService;
    private NapoleonBonaparteInspiredData data = new NapoleonBonaparteInspiredData();

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        User napoleon = data.napoleonBonaparte();
        when(authenticationService.getCurrentUserOrDie()).thenReturn(napoleon);
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


    @Deprecated
    public ResultActions checkValidationError(ResultActions request, String fieldName) throws Exception {
        return request
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isArray())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").isNotEmpty())
                .andExpect(jsonPath("$.field").value(fieldName));
    }

    @Deprecated
    public ResultActions checkValue(ResultActions request, String path, String value) throws Exception {
        return request
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath(path).exists())
                .andExpect(jsonPath(path).isNotEmpty())
                .andExpect(jsonPath(path).value(value));
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, autenticado")
    public void fullRequest_Authenticated() throws Exception {
        var request = fullRequestFile();
        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        );

        checkValue(response, "$.username", data.napoleonBonaparte().getUsername());
        checkValue(response, "$.name", request.getName());
        checkValue(response, "$.neighbourhood.name", request.getNeighbourhood().getName());
        checkValue(response, "$.picture.description", request.getPicture().getDescription());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud parcial, autenticado")
    public void partialRequest_Authenticated() throws Exception {
        var fullRequest = fullRequestFile();
        var requestWithOnlyName = new UserPatchEditRequest();
        requestWithOnlyName.setName(fullRequest.getName());
        var firstResponse = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(requestWithOnlyName))
        );

        checkValue(firstResponse, "$.username", data.napoleonBonaparte().getUsername());
        checkValue(firstResponse, "$.name", requestWithOnlyName.getName());
        checkValue(firstResponse, "$.neighbourhood.name", data.napoleonBonaparte().getNeighbourhood().getName());
        checkValue(firstResponse, "$.picture.description", data.napoleonBonaparte().getProfilePicture().getDescription());

        assertFalse(firstResponse.andReturn().getResponse().getContentAsString().isBlank());

        var requestOnlyAddress = new UserPatchEditRequest();
        requestOnlyAddress.setAddress(fullRequest.getAddress());
        var secondResponse = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(requestOnlyAddress))
        );

        checkValue(secondResponse, "$.username", data.napoleonBonaparte().getUsername());
        checkValue(secondResponse, "$.address", requestOnlyAddress.getAddress());
        checkValue(secondResponse, "$.neighbourhood.name", data.napoleonBonaparte().getNeighbourhood().getName());
        checkValue(secondResponse, "$.picture.description", data.napoleonBonaparte().getProfilePicture().getDescription());

        assertFalse(secondResponse.andReturn().getResponse().getContentAsString().isBlank());

        var requestWithOnlyNeighbourhood = new UserPatchEditRequest();
        requestWithOnlyNeighbourhood.setNeighbourhood(data.villaDeiMulini());
        var thirdResponse = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(requestWithOnlyNeighbourhood))
        );

        checkValue(thirdResponse, "$.username", data.napoleonBonaparte().getUsername());
        checkValue(thirdResponse, "$.neighbourhood.name", requestWithOnlyNeighbourhood.getNeighbourhood().getName());
        checkValue(thirdResponse, "$.picture.description", data.napoleonBonaparte().getProfilePicture().getDescription());

        assertFalse(thirdResponse.andReturn().getResponse().getContentAsString().isBlank());

        var requestWithOnlyPicture = new UserPatchEditRequest();
        requestWithOnlyPicture.setPicture(data.napoleonCruzandoLosAlpes());
        var fourthResponse = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(requestWithOnlyPicture))
        );

        checkValue(fourthResponse, "$.username", data.napoleonBonaparte().getUsername());
        checkValue(fourthResponse, "$.neighbourhood.name", data.napoleonBonaparte().getNeighbourhood().getName());
        checkValue(fourthResponse, "$.picture.description", requestWithOnlyPicture.getPicture().getDescription());

        assertFalse(fourthResponse.andReturn().getResponse().getContentAsString().isBlank());

    }

    @Test
    @DisplayName("Modificar usuario: Usuario inactivo, autenticado")
    public void inactiveUser_Authenticated() throws Exception {
        User napoleon = data.napoleonBonaparte();
        napoleon.setActive(false);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(napoleon);
        when(userRepository.findByUsername(napoleon.getUsername())).thenReturn(Optional.of(napoleon));

        var request = fullRequestFile();
        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        )
                .andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Usuario no existente, autenticado")
    public void userNotFound_Authenticated() throws Exception {
        User napoleon = data.napoleonBonaparte();
        napoleon.setActive(false);
        when(authenticationService.getCurrentUser()).thenReturn(Optional.empty());
        when(userRepository.findByUsername(napoleon.getUsername())).thenReturn(Optional.empty());

        var request = fullRequestFile();
        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        )
                .andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Nombre incorrecto, autenticado")
    public void invalidName_Authenticated() throws Exception {
        var request = fullRequestFile();
        request.setName("$%SDAFSADF s f-2 .. s");
        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").isArray())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.field").exists())
                .andExpect(jsonPath("$.field").isNotEmpty())
                .andExpect(jsonPath("$.absolutelyInexistentPath.lalala").exists())
                .andExpect(jsonPath("$.field").value("name"))
                .andReturn();
        assertFalse(response.getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Barrio no encontrado, autenticado")
    public void neighbourhoodNotFound_Authenticated() throws Exception {
        var request = fullRequestFile();
        var neighbourhood = data.longwood();
        neighbourhood.setId(9999999999L);
        request.setNeighbourhood(neighbourhood);
        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        checkValidationError(response, "neighbourhood");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Barrio inactivo, autenticado")
    public void inactiveNeighbourhood_Authenticated() throws Exception {
        var request = fullRequestFile();
        var neighbourhood = data.longwood();
        neighbourhood.setActive(false);

        when(neighbourhoodRepository.findById(data.longwood().getId())).thenReturn(Optional.of(neighbourhood));
        when(neighbourhoodRepository.findActiveNeighbourhoodById(data.longwood().getId())).thenReturn(Optional.empty());

        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        checkValidationError(response, "neighbourhood");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Imagen inactiva, autenticado")
    public void inactiveImage_Authenticated() throws Exception {
        var request = fullRequestFile();
        var picture = data.autorretrato();
        picture.setActive(false);

        when(pictureRepository.findById(data.autorretrato().getId())).thenReturn(Optional.of(picture));

        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        checkValidationError(response, "picture");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Imagen no encontrada, autenticado")
    public void imageNotFound_Authenticated() throws Exception {
        var request = fullRequestFile();
        var picture = data.autorretrato();

        when(pictureRepository.findById(data.autorretrato().getId())).thenReturn(Optional.empty());

        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        checkValidationError(response, "picture");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Sin derechos sobre la imagen, autenticado")
    public void noUseRightsOverImage_Authenticated() throws Exception {
        var request = fullRequestFile();
        var picture = data.autorretratoDeOtraPersona();

        var response = mockMvc.perform(patch("/api/users/me")
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        checkValidationError(response, "picture");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario administrador. ")
    public void fullRequest() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.ADMIN);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().isOk());
        checkValue(response, "$.username", data.mariaLuisaDeAustria().getUsername());
        checkValue(response, "$.name", request.getName());
        checkValue(response, "$.address", request.getAddress());
        checkValue(response, "$.picture.description", request.getPicture().getDescription());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario moderador. ")
    public void fullRequestAsModerator() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.MOD);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario entidad gubernamental. ")
    public void fullRequestAsGovt() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.GOVT);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario organización. ")
    public void fullRequestAsOrganization() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.ORGANIZATION);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario contribuidor. ")
    public void fullRequestAsContributor() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.CONTRIBUTOR);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario vecino. ")
    public void fullRequestAsNeighbour() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.NEIGHBOUR);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }


    @Test
    @DisplayName("Modificar usuario: Solicitud completa, usuario sin verificar. ")
    public void fullRequestAsUser() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.USER);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var response = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Solicitud parcial, autenticado")
    public void partialRequest() throws Exception {
        var fullRequest = fullRequestFile();
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.ADMIN);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var requestWithOnlyName = new UserPatchEditRequest();
        requestWithOnlyName.setName(fullRequest.getName());
        var firstResponse = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(requestWithOnlyName))
        );

        checkValue(firstResponse, "$.username", data.mariaLuisaDeAustria().getUsername());
        checkValue(firstResponse, "$.name", requestWithOnlyName.getName());
        checkValue(firstResponse, "$.neighbourhood.name", data.mariaLuisaDeAustria().getNeighbourhood().getName());
        checkValue(firstResponse, "$.picture.description", data.mariaLuisaDeAustria().getProfilePicture().getDescription());

        assertFalse(firstResponse.andReturn().getResponse().getContentAsString().isBlank());

        var requestOnlyAddress = new UserPatchEditRequest();
        requestOnlyAddress.setAddress(fullRequest.getAddress());
        var secondResponse = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(requestOnlyAddress))
        );

        checkValue(secondResponse, "$.username", data.mariaLuisaDeAustria().getUsername());
        checkValue(secondResponse, "$.address", requestOnlyAddress.getAddress());
        checkValue(secondResponse, "$.neighbourhood.name", data.mariaLuisaDeAustria().getNeighbourhood().getName());
        checkValue(secondResponse, "$.picture.description", data.mariaLuisaDeAustria().getProfilePicture().getDescription());

        assertFalse(secondResponse.andReturn().getResponse().getContentAsString().isBlank());

        var requestWithOnlyNeighbourhood = new UserPatchEditRequest();
        requestWithOnlyNeighbourhood.setNeighbourhood(data.villaDeiMulini());
        var thirdResponse = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(requestWithOnlyNeighbourhood))
        );

        checkValue(thirdResponse, "$.username", data.mariaLuisaDeAustria().getUsername());
        checkValue(thirdResponse, "$.neighbourhood.name", requestWithOnlyNeighbourhood.getNeighbourhood().getName());
        checkValue(thirdResponse, "$.picture.description", data.mariaLuisaDeAustria().getProfilePicture().getDescription());

        assertFalse(thirdResponse.andReturn().getResponse().getContentAsString().isBlank());

        var requestWithOnlyPicture = new UserPatchEditRequest();
        requestWithOnlyPicture.setPicture(data.autorretratoDeOtraPersona());
        var fourthResponse = mockMvc.perform(patch("/api/users/" + data.mariaLuisaDeAustria().getUsername())
                .contentType("application/json")
                .content(asJsonString(requestWithOnlyPicture))
        );

        checkValue(fourthResponse, "$.username", data.mariaLuisaDeAustria().getUsername());
        checkValue(fourthResponse, "$.neighbourhood.name", data.mariaLuisaDeAustria().getNeighbourhood().getName());
        checkValue(fourthResponse, "$.picture.description", requestWithOnlyPicture.getPicture().getDescription());

        assertFalse(fourthResponse.andReturn().getResponse().getContentAsString().isBlank());

    }


    @Test
    @DisplayName("Modificar usuario: Usuario inactivo")
    public void inactiveUser() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.ADMIN);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var maria = data.mariaLuisaDeAustria();
        maria.setActive(false);
        when(userRepository.findByUsername(maria.getUsername())).thenReturn(Optional.of(maria));
        var response = mockMvc.perform(patch("/api/users/" + maria.getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Usuario no existente")
    public void userNotFound() throws Exception {
        var request = fullRequestFile();
        var me = data.napoleonBonaparte();
        me.setRole(Role.ADMIN);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var maria = data.mariaLuisaDeAustria();
        maria.setActive(false);
        when(userRepository.findByUsername(maria.getUsername())).thenReturn(Optional.empty());
        var response = mockMvc.perform(patch("/api/users/" + maria.getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Nombre incorrecto")
    public void invalidName() throws Exception {
        var request = new UserPatchEditRequest();
        request.setName("%%%$FDSFADFDASFsa231es");
        var me = data.napoleonBonaparte();
        me.setRole(Role.ADMIN);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var maria = data.mariaLuisaDeAustria();
        var response = mockMvc.perform(patch("/api/users/" + maria.getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(status().is4xxClientError());
        checkValidationError(response, "name");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Barrio no encontrado")
    public void neighbourhoodNotFound() throws Exception {
        var request = new UserPatchEditRequest();
        var neighbourhood = data.longwood();
        neighbourhood.setId(9999999999L);
        request.setNeighbourhood(neighbourhood);
        var me = data.napoleonBonaparte();
        me.setRole(Role.ADMIN);
        when(userRepository.findByUsername(me.getUsername())).thenReturn(Optional.of(me));
        var maria = data.mariaLuisaDeAustria();
        var response = mockMvc.perform(patch("/api/users/" + maria.getUsername())
                .contentType("application/json")
                .content(asJsonString(request))
        );
        response.andExpect(jsonPath("$.field").value("name"));
        response.andExpect(status().isOk());
        checkValidationError(response, "name");
        assertFalse(response.andReturn().getResponse().getContentAsString().isBlank());
    }

    @Test
    @DisplayName("Modificar usuario: Barrio inactivo")
    public void inactiveNeighbourhood() throws Exception {}

    @Test
    @DisplayName("Modificar usuario: Imagen inactiva")
    public void inactiveImage() throws Exception {}

    @Test
    @DisplayName("Modificar usuario: Imagen no encontrada")
    public void imageNotFound() throws Exception {}

    @Test
    @DisplayName("Modificar usuario: Sin derechos sobre la imagen")
    public void noUseRightsOverImage() throws Exception {}

}
