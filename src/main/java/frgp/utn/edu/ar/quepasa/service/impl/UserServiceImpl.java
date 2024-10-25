package frgp.utn.edu.ar.quepasa.service.impl;


import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.RequestStatus;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.request.RoleUpdateRequest;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.repository.request.RoleUpdateRequestRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import frgp.utn.edu.ar.quepasa.service.request.RoleUpdateRequestService;
import frgp.utn.edu.ar.quepasa.service.validators.objects.NeighbourhoodValidator;
import frgp.utn.edu.ar.quepasa.service.validators.objects.PictureValidator;
import frgp.utn.edu.ar.quepasa.service.validators.users.NameValidator;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService, RoleUpdateRequestService {

    private AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final PictureRepository pictureRepository;
    private final RoleUpdateRequestRepository roleUpdateRequestRepository;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            NeighbourhoodRepository neighbourhoodRepository,
            PictureRepository pictureRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.neighbourhoodRepository = neighbourhoodRepository;
        this.pictureRepository = pictureRepository;
        this.roleUpdateRequestRepository = null;
    }

    @Autowired @Lazy
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }

    @Override
    public Page<User> search(String query, Pageable pageable) {
        return userRepository.search(query, pageable, true);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * <b>Edita un usuario</b>
     * <p>Recibe el nombre de usuario del objetivo, y los datos nuevos.</p>
     */
    @Override
    public User update(String username, UserPatchEditRequest data) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Fail("User not found. ", HttpStatus.NOT_FOUND));
        return update(data, user);
    }

    /**
     * <b>Edita un usuario</b>
     * <p>De uso interno, realiza las modificaciones a partir de la solicitud. </p>
     */
    @Override
    public User update(@NotNull UserPatchEditRequest data, @NotNull User user) {
        if(data.getName() != null) {
            var name = new NameValidator(data.getName())
                    .validateCompoundNames()
                    .build();
            user.setName(name);
        }
        if(data.getAddress() != null) user.setAddress(data.getAddress());
        if(data.getNeighbourhood() != null) {
            var neighbourhood = new NeighbourhoodValidator(data.getNeighbourhood())
                    .isActive(neighbourhoodRepository)
                    .build();
            user.setNeighbourhood(neighbourhood);
        }
        if(data.getPicture() != null) {
            var picture = new PictureValidator(data.getPicture())
                    .isActive(pictureRepository)
                    .isOwner(pictureRepository, user, userRepository)
                    .build();
            user.setProfilePicture(picture);
        }
        var u = userRepository.save(user);
        return u;
    }

    /**
     * <b>Edita al usuario autenticado</b>
     * <p>Intenta editar el usuario en sesión. </p>
     */
    @Override
    public User update(UserPatchEditRequest data) {
        User user = authenticationService.getCurrentUserOrDie();
        return update(data, user);
    }

    @Override
    public void updatePassword(String newPassword) {
        User user = authenticationService.getCurrentUserOrDie();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Override
    @Deprecated
    public User save(User u) {
        return userRepository.save(u);
    }

    @Override
    public void delete(String username) {
        User user = findByUsername(username);
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void delete() {
        User current = authenticationService.getCurrentUserOrDie();
        current.setActive(false);
        userRepository.save(current);
    }


    @Override
    public RoleUpdateRequest createRoleUpdateRequest(Role requestedRole, String remarks) {
        User currentUser = authenticationService.getCurrentUserOrDie();

        if (currentUser.getRole() == Role.ADMIN) {
            throw new Fail("Los administradores no pueden solicitar una actualizacion de rol.", HttpStatus.FORBIDDEN);
        }
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRequester(currentUser);
        request.setRequestedRole(requestedRole);
        request.setRemarks(remarks);
        request.setStatus(RequestStatus.WAITING);

        return roleUpdateRequestRepository.save(request);
    }


    @Override
    public void reviewRoleUpdateRequest(UUID requestId, boolean approve, String adminRemarks) {
        RoleUpdateRequest roleUpdateRequest = roleUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new Fail("Solicitud d erol no encontrada", HttpStatus.NOT_FOUND));

        if (roleUpdateRequest.getStatus() != RequestStatus.WAITING) {
            throw new Fail("La solicitud ya se proceso.", HttpStatus.BAD_REQUEST);
        }
        if (approve) {
            roleUpdateRequest.setStatus(RequestStatus.APPROVED);
            roleUpdateRequest.getRequester().setRole(roleUpdateRequest.getRequestedRole());
            roleUpdateRequest.setRemarks(adminRemarks);
        } else {
            roleUpdateRequest.setStatus(RequestStatus.REJECTED);
            roleUpdateRequest.setRemarks(adminRemarks);
        }
        roleUpdateRequest.setReviewer(authenticationService.getCurrentUserOrDie());
        roleUpdateRequestRepository.save(roleUpdateRequest);
    }

    @Override
    public RoleUpdateRequest respondToRoleUpdateRequest(UUID requestId, boolean approve, String reviewerRemarks) {
        User currentUser = authenticationService.getCurrentUserOrDie();
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.MOD) {
            throw new Fail("No tiene permiso para revisar esta solicitud.", HttpStatus.FORBIDDEN);
        }

        RoleUpdateRequest request = roleUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new Fail("Solicitud no encontrada", HttpStatus.NOT_FOUND));

        if (request.getStatus() != RequestStatus.WAITING) {
            throw new Fail("Esta solicitud ya ha sido procesada.", HttpStatus.BAD_REQUEST);
        }
        if (approve) {
            request.setStatus(RequestStatus.APPROVED);
            User requester = request.getRequester();
            requester.setRole(request.getRequestedRole());
            userRepository.save(requester);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }
        request.setReviewer(currentUser);
        request.setRemarks(reviewerRemarks);
        return roleUpdateRequestRepository.save(request);
    }
    @Override
    public void deleteRoleUpdateRequest(UUID requestId) {
        User currentUser = authenticationService.getCurrentUserOrDie();
        RoleUpdateRequest request = roleUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new Fail("Solicitud no encontrada", HttpStatus.NOT_FOUND));
        if (!currentUser.getUsername().equals(request.getRequester().getUsername()) 
                && currentUser.getRole() != Role.ADMIN) {
            throw new Fail("No tiene permiso para eliminar esta solicitud.", HttpStatus.FORBIDDEN);
        }
        request.setActive(false);
        roleUpdateRequestRepository.save(request);
    }
    @Override
    public List<RoleUpdateRequest> getUserRequests() {
        User currentUser = authenticationService.getCurrentUserOrDie();
        return roleUpdateRequestRepository.findByRequesterAndActiveTrue(currentUser);
    }
    
    @Override
    public List<RoleUpdateRequest> getAllRequests() {
        User currentUser = authenticationService.getCurrentUserOrDie();    
        if (currentUser.getRole() != Role.ADMIN) {
            throw new Fail("No tiene permiso para ver todas las solicitudes.", HttpStatus.FORBIDDEN);
        }
        return roleUpdateRequestRepository.findAllByActiveTrue();
    }
}
