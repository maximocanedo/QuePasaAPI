package frgp.utn.edu.ar.quepasa.controller.requests;
import frgp.utn.edu.ar.quepasa.controller.request.RoleUpdateRequestController;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleUpdateRequestControllerTest {

    @Mock
    private RoleUpdateRequestService roleUpdateRequestService;

    @InjectMocks
    private RoleUpdateRequestController roleUpdateRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoleRequest() {
        Role requestedRole = Role.ADMIN;
        String remarks = "Promoción a administrador";
        RoleUpdateRequest request = new RoleUpdateRequest();
        
        when(roleUpdateRequestService.createRoleUpdateRequest(requestedRole, remarks)).thenReturn(request);

        ResponseEntity<RoleUpdateRequest> response = roleUpdateRequestController.createRoleRequest(requestedRole, remarks);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request, response.getBody());
        verify(roleUpdateRequestService, times(1)).createRoleUpdateRequest(requestedRole, remarks);
    }

    
    @Test
    void testRespondToRoleRequest() {
        UUID requestId = UUID.randomUUID();
        boolean approve = true;
        String reviewerRemarks = "Aprobado";
        RoleUpdateRequest updatedRequest = new RoleUpdateRequest();

        
        when(roleUpdateRequestService.respondToRoleUpdateRequest(requestId, approve, reviewerRemarks)).thenReturn(updatedRequest);

        ResponseEntity<RoleUpdateRequest> response = roleUpdateRequestController.respondToRoleRequest(requestId, approve, reviewerRemarks);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRequest, response.getBody());
        verify(roleUpdateRequestService, times(1)).respondToRoleUpdateRequest(requestId, approve, reviewerRemarks);
    }

    @Test
    void testDeleteRoleRequest() {
        UUID requestId = UUID.randomUUID();

        doNothing().when(roleUpdateRequestService).deleteRoleUpdateRequest(requestId);

        ResponseEntity<Void> response = roleUpdateRequestController.deleteRoleRequest(requestId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleUpdateRequestService, times(1)).deleteRoleUpdateRequest(requestId);
    }


    @Test
    void testGetMyRequests() {
        List<RoleUpdateRequest> requests = List.of(new RoleUpdateRequest(), new RoleUpdateRequest());

        when(roleUpdateRequestService.getUserRequests()).thenReturn(requests);

        ResponseEntity<List<RoleUpdateRequest>> response = roleUpdateRequestController.getMyRequests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requests, response.getBody());
        verify(roleUpdateRequestService, times(1)).getUserRequests();
    }


    @Test
    void testGetAllRequests() {
        List<RoleUpdateRequest> requests = List.of(new RoleUpdateRequest(), new RoleUpdateRequest());

        when(roleUpdateRequestService.getAllRequests()).thenReturn(requests);

        ResponseEntity<List<RoleUpdateRequest>> response = roleUpdateRequestController.getAllRequests();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(requests, response.getBody());
        verify(roleUpdateRequestService, times(1)).getAllRequests();
    }
}































