package frgp.utn.edu.ar.quepasa.service.impl.request;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.RequestStatus;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.repository.request.RoleUpdateRequestRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.RoleUpdateRequestService;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import org.springframework.http.HttpStatus;

@Deprecated
@Service
public class RoleUpdateRequestServiceImpl implements RoleUpdateRequestService {

    private final RoleUpdateRequestRepository roleUpdateRequestRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public RoleUpdateRequestServiceImpl(RoleUpdateRequestRepository roleUpdateRequestRepository, UserRepository userRepository, AuthenticationService authenticationService) {
        this.roleUpdateRequestRepository = roleUpdateRequestRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    /**
     * Crea una nueva solicitud de actualizacion de rol.
     * 
     * @param requestedRole Rol solicitado.
     * @param remarks       Comentarios sobre la solicitud.
     * @return La solicitud creada.
     */
    @Deprecated
    @Override
    public RoleUpdateRequest create(Role requestedRole, String remarks) {
        User requester = getCurrentUser();
        Role currentRole = requester.getRole();
    
        if (requestedRole == Role.MOD || requestedRole == Role.ADMIN) {
            throw new Fail("No se permite solicitar el rol de MOD o ADMIN.", HttpStatus.BAD_REQUEST);
        }
        if (requestedRole.ordinal() < currentRole.ordinal()) {
            throw new Fail("El rol solicitado no puede ser inferior que el rol actual.", HttpStatus.BAD_REQUEST);
        }
    
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRequestedRole(requestedRole);
        request.setRemarks(remarks);
        request.setRequester(requester);
        request.setActive(true);
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
    @Deprecated
    @Override
    public void review(UUID requestId, boolean approve, String adminRemarks) {
        Optional<RoleUpdateRequest> optionalRequest = roleUpdateRequestRepository.findById(requestId);

        if (optionalRequest.isPresent()) {
            RoleUpdateRequest request = optionalRequest.get();
            request.setStatus(approve ? RequestStatus.APPROVED : RequestStatus.REJECTED);
            request.setRemarks(adminRemarks);
            roleUpdateRequestRepository.save(request);
        } else {
            throw new Fail("RoleUpdateRequest con ID " + requestId + " no encontrado", HttpStatus.NOT_FOUND);
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
    @Deprecated
    @Override
    public RoleUpdateRequest close(UUID requestId, boolean approve, String reviewerRemarks) {
        Optional<RoleUpdateRequest> optionalRequest = roleUpdateRequestRepository.findById(requestId);
    
        if (optionalRequest.isPresent()) {
            RoleUpdateRequest request = optionalRequest.get();
            if (request.getStatus() != RequestStatus.WAITING) {
                throw new Fail("La solicitud ya ha sido revisada y no puede modificarse.", HttpStatus.BAD_REQUEST);
            }
            request.setStatus(approve ? RequestStatus.APPROVED : RequestStatus.REJECTED);
            request.setReviewer(getCurrentUser());
            request.setRemarks(reviewerRemarks);

            if(approve) {
                User user = optionalRequest.get().getOwner();
                user.setRole(optionalRequest.get().getRequestedRole());
                userRepository.save(user);
            }

            return roleUpdateRequestRepository.save(request);
        }
    
        throw new Fail("RoleUpdateRequest: " + requestId + " no encontrado", HttpStatus.NOT_FOUND);
    }
    

    /**
     * Elimina lógicamente una solicitud de actualización de rol.
     * 
     * @param requestId ID de la solicitud a eliminar.
     * @throws IllegalArgumentException si no se encontró la solicitud.
     */
    @Deprecated
    @Override
    public void delete(UUID requestId) {
        Optional<RoleUpdateRequest> optionalRequest = roleUpdateRequestRepository.findById(requestId);

        if (optionalRequest.isPresent()) {
            RoleUpdateRequest request = optionalRequest.get();
            request.setActive(false);
            roleUpdateRequestRepository.save(request);
        } else {
            throw new Fail("RoleUpdateRequest: " + requestId + " no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Devuelve todas las solicitudes de actualizacion de rol realizadas por el usuario
     * actual.
     * 
     * @return Un listado de solicitudes de actualizacion de rol.
     */
    @Deprecated
    @Override
    public List<RoleUpdateRequest> findByUser() {
        User currentUser = authenticationService.getCurrentUserOrDie();
        return roleUpdateRequestRepository.findByRequesterAndActiveTrue(currentUser);
    }

    /**
     * Devuelve todas las solicitudes de actualizacion de rol en estado activo.
     * 
     * @return Un listado de solicitudes de actualizacion de rol.
     */
    @Deprecated
    @Override
    public List<RoleUpdateRequest> findAll() {
        return roleUpdateRequestRepository.findAllByActiveTrue();
    }


    @Deprecated(forRemoval = true)
    private User getCurrentUser() {
        return authenticationService.getCurrentUserOrDie();
    }


}
