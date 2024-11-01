package frgp.utn.edu.ar.quepasa.controller.requests;

import java.util.List;
import java.util.UUID;

import frgp.utn.edu.ar.quepasa.service.RoleUpdateRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import frgp.utn.edu.ar.quepasa.model.enums.RequestStatus;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controlador de RoleUpdateRequest")
public class RoleUpdateRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RoleUpdateRequestService roleUpdateRequestService;

    private RoleUpdateRequest sampleRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleRequest = new RoleUpdateRequest();
        sampleRequest.setId(UUID.randomUUID());
        sampleRequest.setRequestedRole(Role.ADMIN);
        sampleRequest.setStatus(RequestStatus.WAITING);
        sampleRequest.setRemarks("Requesting Admin Role");
    }

    @Test
    public void testCreateRoleRequest() throws Exception {
        when(roleUpdateRequestService.create(Role.ADMIN, "Requesting Admin Role"))
                .thenReturn(sampleRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/request/role/request")
                .param("requestedRole", Role.ADMIN.name())
                .param("remarks", "Requesting Admin Role")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestedRole").value(Role.ADMIN.name()))
                .andExpect(jsonPath("$.remarks").value("Requesting Admin Role"));
    }

    @Test
    public void testRespondToRoleRequest() throws Exception {
        UUID requestId = UUID.randomUUID();
        when(roleUpdateRequestService.close(requestId, true, "Approved by admin"))
                .thenReturn(sampleRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/request/role/respond")
                .param("requestId", requestId.toString())
                .param("approve", "true")
                .param("reviewerRemarks", "Approved by admin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RequestStatus.WAITING.name()));
    }

    @Test
    public void testDeleteRoleRequest() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/request/role/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(roleUpdateRequestService, times(1)).delete(requestId);
    }

    @Test
    public void testGetMyRequests() throws Exception {
        when(roleUpdateRequestService.findByUser()).thenReturn(List.of(sampleRequest));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/request/role/my-requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestedRole").value(Role.ADMIN.name()));
    }

    @Test
    public void testGetAllRequests() throws Exception {
        when(roleUpdateRequestService.findAll()).thenReturn(List.of(sampleRequest));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/request/role/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestedRole").value(Role.ADMIN.name()));
    }
}
