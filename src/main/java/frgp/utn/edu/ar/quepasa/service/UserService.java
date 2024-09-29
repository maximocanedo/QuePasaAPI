package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Page<User> listUser(Pageable pageable);

    User findByUsername(String username);
    User update(UserPatchEditRequest user);
    User update(String username, UserPatchEditRequest user);

    void updatePassword(String newPassword);

    void delete(String username);
}
