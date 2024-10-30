package frgp.utn.edu.ar.quepasa.service.impl.request;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.RequestStatus;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.repository.request.RoleUpdateRequestRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;

@Service
@Primary
public class RoleUpdateRequestServiceImpl implements RoleUpdateRequestService {

    private final RoleUpdateRequestRepository roleUpdateRequestRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public RoleUpdateRequestServiceImpl(RoleUpdateRequestRepository roleUpdateRequestRepository, AuthenticationService authenticationService) {
        this.roleUpdateRequestRepository = roleUpdateRequestRepository;
        this.authenticationService = authenticationService;
    }

    /**
     * Crea una nueva solicitud de actualizacion de rol.
     * 
     * @param requestedRole Rol solicitado.
     * @param remarks       Comentarios sobre la solicitud.
     * @return La solicitud creada.
     */
    @Override
    public RoleUpdateRequest createRoleUpdateRequest(Role requestedRole, String remarks) {
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRequestedRole(requestedRole);
        request.setRemarks(remarks);
        request.setStatus(RequestStatus.WAITING);
        return roleUpdateRequestRepository.save(request);
    }

    /**
     * Revisa una solicitud de actualizacion de rol.
     * 
     * @param requestId   ID de la solicitud a revisar.
     * @param approve     True si se aprueba, False si se rechaza.
     * @param adminRemarks Comentarios del administrador.
     * @throws IllegalArgumentException si no se encontr&oacute; la solicitud.
     */
    @Override
    public void reviewRoleUpdateRequest(UUID requestId, boolean approve, String adminRemarks) {
        Optional<RoleUpdateRequest> optionalRequest = roleUpdateRequestRepository.findById(requestId);

        if (optionalRequest.isPresent()) {
            RoleUpdateRequest request = optionalRequest.get();
            request.setStatus(approve ? RequestStatus.APPROVED : RequestStatus.REJECTED);
            request.setRemarks(adminRemarks);
            roleUpdateRequestRepository.save(request);
        } else {
            throw new IllegalArgumentException("RoleUpdateRequest con ID " + requestId + " no enocontrado");
        }
    }

    /**
     * Responde a una solicitud de actualizacion de rol.
     * 
     * @param requestId       ID de la solicitud a responder.
     * @param approve         True si se aprueba, False si se rechaza.
     * @param reviewerRemarks Comentarios del evaluador.
     * @return La solicitud actualizada.
     * @throws IllegalArgumentException si no se encontro la solicitud.
     */
    @Override
    public RoleUpdateRequest respondToRoleUpdateRequest(UUID requestId, boolean approve, String reviewerRemarks) {
        Optional<RoleUpdateRequest> optionalRequest = roleUpdateRequestRepository.findById(requestId);

        if (optionalRequest.isPresent()) {
            RoleUpdateRequest request = optionalRequest.get();
            request.setStatus(approve ? RequestStatus.APPROVED : RequestStatus.REJECTED);
            request.setRemarks(reviewerRemarks);
            return roleUpdateRequestRepository.save(request);
        }

        throw new IllegalArgumentException("RoleUpdateRequest ID " + requestId + " no enocontrado");
    }

    /**
     * Elimina lógicamente una solicitud de actualización de rol.
     * 
     * @param requestId ID de la solicitud a eliminar.
     * @throws IllegalArgumentException si no se encontró la solicitud.
     */
    @Override
    public void deleteRoleUpdateRequest(UUID requestId) {
        Optional<RoleUpdateRequest> optionalRequest = roleUpdateRequestRepository.findById(requestId);

        if (optionalRequest.isPresent()) {
            RoleUpdateRequest request = optionalRequest.get();
            request.setActive(false);
            roleUpdateRequestRepository.save(request);
        } else {
            throw new IllegalArgumentException("RoleUpdateRequest con ID " + requestId + " no enocontrado");
        }
    }

    /**
     * Devuelve todas las solicitudes de actualizacion de rol realizadas por el usuario
     * actual.
     * 
     * @return Un listado de solicitudes de actualizacion de rol.
     */
    @Override
    public List<RoleUpdateRequest> getUserRequests() {
        User currentUser = getCurrentUser();
        return roleUpdateRequestRepository.findByRequesterAndActiveTrue(currentUser);
    }

    /**
     * Devuelve todas las solicitudes de actualizacion de rol en estado activo.
     * 
     * @return Un listado de solicitudes de actualizacion de rol.
     */
    @Override
    public List<RoleUpdateRequest> getAllRequests() {
        return roleUpdateRequestRepository.findAllByActiveTrue();
    }


    private User getCurrentUser() {
        return authenticationService.getCurrentUserOrDie();
    }
}
