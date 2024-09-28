package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Auth {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) { return Optional.empty(); }
        if(authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails)authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }

}
