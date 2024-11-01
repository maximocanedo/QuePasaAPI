package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.repository.request.RoleUpdateRequestRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import quepasa.api.validators.commons.ActivatableValidator;
import quepasa.api.validators.users.NameValidator;

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
public class UserServiceImpl implements UserService {

    private AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final PictureRepository pictureRepository;
    private OwnerService ownerService;

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
    }

    @Autowired @Lazy
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired @Lazy
    public void setOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
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
            var neighbourhood = neighbourhoodRepository
                    .findActiveNeighbourhoodById(data.getNeighbourhood().getId())
                    .orElseThrow(() -> new Fail("Neighbourhood not found. ", HttpStatus.BAD_REQUEST));
            user.setNeighbourhood(neighbourhood);
        }
        if(data.getPicture() != null) {
            Picture pic = (Picture) new ActivatableValidator(
                    pictureRepository
                    .findById(data.getPicture().getId())
                    .orElseThrow(() -> new Fail("Picture not found. "))
            ).isActive().build();
            ownerService.of(pic)
                    .isOwner()
                    .orElseFail();
            user.setProfilePicture(pic);
        }
        return userRepository.save(user);
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

}
