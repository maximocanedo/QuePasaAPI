package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    User findByUsername(String username);
    User update(String username, User user);
    void delete(String username);
}
