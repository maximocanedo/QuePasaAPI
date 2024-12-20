package frgp.utn.edu.ar.quepasa.service.requests;

/* import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.request.RoleUpdateRequestRepository;
import frgp.utn.edu.ar.quepasa.service.impl.UserServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.request.RoleUpdateRequestServiceImpl;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("RoleUpdateRequestServiceTest") */
@Deprecated
class RoleUpdateRequestServiceTest {
/*
    @Autowired
    private MockMvc mockMvc;


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private RoleUpdateRequestServiceImpl roleUpdateRequestService;


    @Mock
    private RoleUpdateRequestRepository roleUpdateRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleUpdateRequestService = mock(RoleUpdateRequestServiceImpl.class);
        setAuthContext();
    }

        
    @Test
    @DisplayName("Test crear solicitud de actualización de rol")
    void testCreate() {
        Role requestedRole = Role.ADMIN;
        String remarks = "Solicito ser administrador";
        String username = "testUser";

        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        RoleUpdateRequest savedRequest = new RoleUpdateRequest();
        savedRequest.setRequestedRole(requestedRole);
        savedRequest.setRemarks(remarks);
        savedRequest.setStatus(RequestStatus.WAITING);
        savedRequest.setRequester(mockUser);

        when(roleUpdateRequestRepository.save(any(RoleUpdateRequest.class))).thenReturn(savedRequest);

        RoleUpdateRequest result = roleUpdateRequestService.create(requestedRole, remarks);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(requestedRole, result.getRequestedRole(), "El rol solicitado debería ser ADMIN");
        assertEquals(remarks, result.getRemarks(), "Las observaciones deberían coincidir");
        assertEquals(RequestStatus.WAITING, result.getStatus(), "El estado debería ser WAITING");

        verify(roleUpdateRequestRepository, times(1)).save(any(RoleUpdateRequest.class));
    }

    @Test
    @DisplayName("Test revisar solicitud de actualización de rol")
    void testReview() {
        UUID requestId = UUID.randomUUID();
        boolean approved = true;
        String reviewerUsername = "adminUser";

        doNothing().when(roleUpdateRequestService).review(requestId, approved, reviewerUsername);

        roleUpdateRequestService.review(requestId, approved, reviewerUsername);
        verify(roleUpdateRequestService, times(1))
            .review(requestId, approved, reviewerUsername);
    }

    @Test
    @DisplayName("Test responder a solicitud de actualización de rol")
    void testClose() {
        UUID requestId = UUID.randomUUID();
        boolean approved = false;
        String response = "Solicitud rechazada";
        RoleUpdateRequest request = new RoleUpdateRequest();

        when(roleUpdateRequestService.close(requestId, approved, response))
            .thenReturn(request);

        RoleUpdateRequest result = roleUpdateRequestService.close(requestId, approved, response);
        assertNotNull(result);
        verify(roleUpdateRequestService, times(1))
            .close(requestId, approved, response);
    }

    @Test
    @DisplayName("Test eliminar solicitud de actualización de rol")
    void testDelete() {
        UUID requestId = UUID.randomUUID();
        doNothing().when(roleUpdateRequestService).delete(requestId);
        roleUpdateRequestService.delete(requestId);
        verify(roleUpdateRequestService, times(1)).delete(requestId);
    }

    @Test
    @DisplayName("Test obtener solicitudes de usuario")
    void testFindByUser() {
        RoleUpdateRequest request = new RoleUpdateRequest();
        List<RoleUpdateRequest> requests = List.of(request);

        when(roleUpdateRequestService.findByUser()).thenReturn(requests);
        List<RoleUpdateRequest> result = roleUpdateRequestService.findByUser();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleUpdateRequestService, times(1)).findByUser();
    }

    @Test
    @DisplayName("Test obtener todas las solicitudes")
    void testFindAll() {
        RoleUpdateRequest request1 = new RoleUpdateRequest();
        RoleUpdateRequest request2 = new RoleUpdateRequest();
        List<RoleUpdateRequest> requests = List.of(request1, request2);

        when(roleUpdateRequestService.findAll()).thenReturn(requests);
        List<RoleUpdateRequest> result = roleUpdateRequestService.findAll();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleUpdateRequestService, times(1)).findAll();
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
    */
}