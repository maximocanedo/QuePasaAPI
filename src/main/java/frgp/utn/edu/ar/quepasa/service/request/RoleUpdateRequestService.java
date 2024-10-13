package frgp.utn.edu.ar.quepasa.service.request;

import java.util.UUID;

import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;

public interface RoleUpdateRequestService {
    RoleUpdateRequest createRoleUpdateRequest(Role requestedRole, String remarks);
    void reviewRoleUpdateRequest(UUID requestId, boolean approve, String adminRemarks);
    RoleUpdateRequest respondToRoleUpdateRequest(UUID requestId, boolean approve, String reviewerRemarks);
}
