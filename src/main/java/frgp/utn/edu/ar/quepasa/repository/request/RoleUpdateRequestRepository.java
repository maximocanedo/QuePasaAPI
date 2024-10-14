package frgp.utn.edu.ar.quepasa.repository.request;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleUpdateRequestRepository extends JpaRepository<RoleUpdateRequest, UUID> {
    List<RoleUpdateRequest> findByRequesterAndActiveTrue(User requester);
    List<RoleUpdateRequest> findAllByActiveTrue();

}
