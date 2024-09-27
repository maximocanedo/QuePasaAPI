package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    User findById(Integer id);
    User findByUsername(String username);
    User updateUser(Integer id, User user);
    void deleteUser(Integer id);
}
