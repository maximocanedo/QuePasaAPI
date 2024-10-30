package frgp.utn.edu.ar.quepasa.service.requests;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.RequestStatus;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.repository.request.RoleUpdateRequestRepository;
import frgp.utn.edu.ar.quepasa.service.impl.UserServiceImpl;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("RoleUpdateRequestServiceTest")
class RoleUpdateRequestServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RoleUpdateRequestService roleUpdateRequestService;
    @InjectMocks private UserServiceImpl userService;

    @Mock
    private RoleUpdateRequestRepository roleUpdateRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setAuthContext();
    }
    
    @Test
    @DisplayName("Test crear solicitud de actualización de rol")
    void testCreateRoleUpdateRequest() {
        Role requestedRole = Role.ADMIN;
        String remarks = "Solicito ser administrador";
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        User foundUser = userService.findByUsername(username);

        RoleUpdateRequest savedRequest = new RoleUpdateRequest();
        savedRequest.setRequestedRole(requestedRole);
        savedRequest.setRemarks(remarks);
        savedRequest.setStatus(RequestStatus.WAITING);
        savedRequest.setRequester(foundUser);


        when(roleUpdateRequestRepository.save(any(RoleUpdateRequest.class))).thenReturn(savedRequest);

        RoleUpdateRequest result = roleUpdateRequestService.createRoleUpdateRequest(requestedRole, remarks);
    
        assertEquals(requestedRole, result.getRequestedRole(), "El rol solicitado debería ser ADMIN");
        assertEquals(remarks, result.getRemarks(), "Las observaciones deberían coincidir");
        assertEquals(RequestStatus.WAITING, result.getStatus(), "El estado debería ser WAITING");

        verify(roleUpdateRequestRepository, times(1)).save(any(RoleUpdateRequest.class));
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