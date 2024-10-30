package frgp.utn.edu.ar.quepasa.repository.request;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;

@Repository
public interface RoleUpdateRequestRepository extends JpaRepository<RoleUpdateRequest, UUID> {

    @Query("SELECT r FROM RoleUpdateRequest r WHERE r.requester = :requester AND r.active = true")
    List<RoleUpdateRequest> findByRequesterAndActiveTrue(@Param("requester") User requester);

    @Query("SELECT r FROM RoleUpdateRequest r WHERE r.active = true")
    List<RoleUpdateRequest> findAllByActiveTrue();
}
