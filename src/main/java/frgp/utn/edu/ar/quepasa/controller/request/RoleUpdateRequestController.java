package frgp.utn.edu.ar.quepasa.controller.request;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;
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
}

