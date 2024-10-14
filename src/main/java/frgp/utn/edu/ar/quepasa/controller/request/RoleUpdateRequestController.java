package frgp.utn.edu.ar.quepasa.controller.request;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/request/role")
public class RoleUpdateRequestController {

    private final RoleUpdateRequestService roleUpdateRequestService;

    @Autowired
    public RoleUpdateRequestController(RoleUpdateRequestService roleUpdateRequestService) {
        this.roleUpdateRequestService = roleUpdateRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<RoleUpdateRequest> createRoleRequest(
            @RequestParam Role requestedRole,
            @RequestParam(required = false) String remarks) {

        RoleUpdateRequest request = roleUpdateRequestService.createRoleUpdateRequest(requestedRole, remarks);
        return ResponseEntity.ok(request);
    }
    @PostMapping("/respond")
    public ResponseEntity<RoleUpdateRequest> respondToRoleRequest(
            @RequestParam UUID requestId,
            @RequestParam boolean approve,
            @RequestParam(required = false) String reviewerRemarks) {
        RoleUpdateRequest updatedRequest = roleUpdateRequestService.respondToRoleUpdateRequest(requestId, approve, reviewerRemarks);
        return ResponseEntity.ok(updatedRequest);
    }
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRoleRequest(@PathVariable UUID requestId) {
        roleUpdateRequestService.deleteRoleUpdateRequest(requestId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/my-requests")
    public ResponseEntity<List<RoleUpdateRequest>> getMyRequests() {
        List<RoleUpdateRequest> requests = roleUpdateRequestService.getUserRequests();
        return ResponseEntity.ok(requests);
    }
    @GetMapping("/all")
    public ResponseEntity<List<RoleUpdateRequest>> getAllRequests() {
        List<RoleUpdateRequest> requests = roleUpdateRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }


}

