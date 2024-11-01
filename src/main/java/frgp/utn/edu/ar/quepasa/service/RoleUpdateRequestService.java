package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface RoleUpdateRequestService {
    RoleUpdateRequest create(Role requestedRole, String remarks);

    void review(UUID requestId, boolean approve, String adminRemarks);

    RoleUpdateRequest close(UUID requestId, boolean approve, String reviewerRemarks);

    void delete(UUID requestId);

    /**
     * Reemplazar por
     * {@code Page<RoleUpdateRequest> findByUser(String q, Pageable pageable); }
     * @return
     */
    @Deprecated List<RoleUpdateRequest> findByUser();

    /**
     * Reemplazar por
     * {@code Page<RoleUpdateRequest> findByUser(String q, Pageable pageable, [Más filtros]); }
     * @return
     */
    @Deprecated List<RoleUpdateRequest> findAll();
}
