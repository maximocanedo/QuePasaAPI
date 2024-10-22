package frgp.utn.edu.ar.quepasa.service.requests;

import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import java.util.concurrent.atomic.AtomicReference;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("RoleUpdateRequestServiceTest")
class RoleUpdateRequestServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RoleUpdateRequestService roleUpdateRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setAuthContext();
    }

    @Test
    @DisplayName("GET /api/request/role - Crear solicitud de actualización de rol")
    void testCreateRoleUpdateRequest() throws Exception {
        RoleUpdateRequest request = new RoleUpdateRequest();

        when(roleUpdateRequestService.createRoleUpdateRequest(any(Role.class), anyString()))
            .thenReturn(request);
        
        mockMvc.perform(get("/api/request/role")
            .param("role", "ADMIN")
            .param("username", "testUser")
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Test revisar solicitud de actualización de rol")
    void testReviewRoleUpdateRequest() {
        UUID requestId = UUID.randomUUID();
        boolean approved = true;
        String reviewerUsername = "adminUser";

        doNothing().when(roleUpdateRequestService).reviewRoleUpdateRequest(requestId, approved, reviewerUsername);

        roleUpdateRequestService.reviewRoleUpdateRequest(requestId, approved, reviewerUsername);
        verify(roleUpdateRequestService, times(1))
            .reviewRoleUpdateRequest(requestId, approved, reviewerUsername);
    }

    @Test
    @DisplayName("Test responder a solicitud de actualización de rol")
    void testRespondToRoleUpdateRequest() {
        UUID requestId = UUID.randomUUID();
        boolean approved = false;
        String response = "Solicitud rechazada";
        RoleUpdateRequest request = new RoleUpdateRequest();

        when(roleUpdateRequestService.respondToRoleUpdateRequest(requestId, approved, response))
            .thenReturn(request);

        RoleUpdateRequest result = roleUpdateRequestService.respondToRoleUpdateRequest(requestId, approved, response);
        assertNotNull(result);
        verify(roleUpdateRequestService, times(1))
            .respondToRoleUpdateRequest(requestId, approved, response);
    }

    @Test
    @DisplayName("Test eliminar solicitud de actualización de rol")
    void testDeleteRoleUpdateRequest() {
        UUID requestId = UUID.randomUUID();
        doNothing().when(roleUpdateRequestService).deleteRoleUpdateRequest(requestId);
        roleUpdateRequestService.deleteRoleUpdateRequest(requestId);
        verify(roleUpdateRequestService, times(1)).deleteRoleUpdateRequest(requestId);
    }

    @Test
    @DisplayName("Test obtener solicitudes de usuario")
    void testGetUserRequests() {
        RoleUpdateRequest request = new RoleUpdateRequest();
        List<RoleUpdateRequest> requests = List.of(request);

        when(roleUpdateRequestService.getUserRequests()).thenReturn(requests);
        List<RoleUpdateRequest> result = roleUpdateRequestService.getUserRequests();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleUpdateRequestService, times(1)).getUserRequests();
    }

    @Test
    @DisplayName("Test obtener todas las solicitudes")
    void testGetAllRequests() {
        RoleUpdateRequest request1 = new RoleUpdateRequest();
        RoleUpdateRequest request2 = new RoleUpdateRequest();
        List<RoleUpdateRequest> requests = List.of(request1, request2);

        when(roleUpdateRequestService.getAllRequests()).thenReturn(requests);
        List<RoleUpdateRequest> result = roleUpdateRequestService.getAllRequests();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleUpdateRequestService, times(1)).getAllRequests();
    }

    private void setAuthContext() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("root")
                .password("123456789")
                .roles("ADMIN")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
