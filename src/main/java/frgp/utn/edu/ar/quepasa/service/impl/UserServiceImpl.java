package frgp.utn.edu.ar.quepasa.service.impl;


import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.Auth;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import frgp.utn.edu.ar.quepasa.service.validators.geo.neighbours.NeighbourhoodObjectValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.pictures.PictureObjectValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.users.NameValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired @Lazy
    private AuthenticationService authenticationService;
    @Autowired
    private Auth auth;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;
    @Autowired
    private PictureRepository pictureRepository;

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
        User user = findByUsername(username);
        return update(data, user);
    }

    /**
     * <b>Edita un usuario</b>
     * <p>De uso interno, realiza las modificaciones a partir de la solicitud. </p>
     */
    @Override
    public User update(UserPatchEditRequest data, User user) {
        if(data.getName() != null) {
            var name = new NameValidatorBuilder(data.getName())
                    .validateCompoundNames()
                    .build();
            user.setName(name);
        }
        if(data.getAddress() != null) user.setAddress(data.getAddress());
        if(data.getNeighbourhood() != null) {
            var neighbourhood = new NeighbourhoodObjectValidatorBuilder(data.getNeighbourhood())
                    .isActive(neighbourhoodRepository)
                    .build();
            user.setNeighbourhood(neighbourhood);
        }
        if(data.getPicture() != null) {
            var picture = new PictureObjectValidatorBuilder(data.getPicture())
                    .isActive(pictureRepository)
                    .isOwner(pictureRepository, user, userRepository)
                    .build();
            user.setProfilePicture(picture);
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
}
