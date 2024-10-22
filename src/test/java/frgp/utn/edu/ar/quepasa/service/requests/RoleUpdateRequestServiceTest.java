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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleUpdateRequestServiceTest {

    @Mock
    private RoleUpdateRequestService roleUpdateRequestService;

    @InjectMocks
    private RoleUpdateRequestServiceTest roleUpdateRequestServiceTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test crear solicitud de actualizacion de rol")
    void testCreateRoleUpdateRequest() {
        // Datos de prueba
        Role role = Role.USER;
        String username = "testUser";
        RoleUpdateRequest request = new RoleUpdateRequest();

        // Simular el comportamiento del servicio
        when(roleUpdateRequestService.createRoleUpdateRequest(any(Role.class), anyString()))
            .thenReturn(request);

        // Llamar al método a probar
        RoleUpdateRequest result = roleUpdateRequestService.createRoleUpdateRequest(role, username);

        // Verificaciones
        assertNotNull(result);
        verify(roleUpdateRequestService, times(1))
            .createRoleUpdateRequest(role, username);
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
    @DisplayName("Test responder a solicitud de actualizacipn de rol")
    void testRespondToRoleUpdateRequest() {

        UUID requestId = UUID.randomUUID();
        boolean approved = false;
        String response = "Solicitud rechazada";
        RoleUpdateRequest request = new RoleUpdateRequest();

        when(roleUpdateRequestService.respondToRoleUpdateRequest(requestId, approved, response))
            .thenReturn(request);

        RoleUpdateRequest result = roleUpdateRequestService.respondToRoleUpdateRequest(requestId, approved, response);
        verify(roleUpdateRequestService, times(1))
            .respondToRoleUpdateRequest(requestId, approved, response);
    }

    @Test
    @DisplayName("Test eliminar solicitud de actualizacion de rol")
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
        verify(roleUpdateRequestService, times(1)).getUserRequests();
    }

    @Test
    @DisplayName("Test obtener todas las solicitudes")
    void testGetAllRequests() {
        RoleUpdateRequest request1 = new RoleUpdateRequest();
        RoleUpdateRequest request2 = new RoleUpdateRequest();
        List<RoleUpdateRequest> requests = List.of(request1, request2);

        when(roleUpdateRequestService.getAllRequests()).thenReturn(requests);
        verify(roleUpdateRequestService, times(1)).getAllRequests();
    }
}



