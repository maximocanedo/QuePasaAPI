package frgp.utn.edu.ar.quepasa.controller.request;

import java.util.List;
import java.util.UUID;

import frgp.utn.edu.ar.quepasa.service.RoleUpdateRequestService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;

@RestController
@RequestMapping("/api/request/role")
@Validated
public class RoleUpdateRequestController {

    private final RoleUpdateRequestService roleUpdateRequestService;

    @Autowired
    public RoleUpdateRequestController(RoleUpdateRequestService roleUpdateRequestService) {
        this.roleUpdateRequestService = roleUpdateRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<RoleUpdateRequest> createRoleRequest(
            @RequestParam @NotNull Role requestedRole,
            @RequestParam(required = false) String remarks) {

        RoleUpdateRequest request = roleUpdateRequestService.create(requestedRole, remarks);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @PostMapping("/respond")
    public ResponseEntity<RoleUpdateRequest> respondToRoleRequest(
            @RequestParam @NotNull UUID requestId,
            @RequestParam boolean approve,
            @RequestParam(required = false) String reviewerRemarks) {

        RoleUpdateRequest updatedRequest = roleUpdateRequestService.close(requestId, approve, reviewerRemarks);
        return ResponseEntity.ok(updatedRequest);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRoleRequest(@PathVariable @NotNull UUID requestId) {
        roleUpdateRequestService.delete(requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<RoleUpdateRequest>> getMyRequests() {
        List<RoleUpdateRequest> requests = roleUpdateRequestService.findByUser();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleUpdateRequest>> getAllRequests() {
        List<RoleUpdateRequest> requests = roleUpdateRequestService.findAll();
        return ResponseEntity.ok(requests);
    }
}
