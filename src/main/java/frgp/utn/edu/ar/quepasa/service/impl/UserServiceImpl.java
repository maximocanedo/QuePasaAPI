package frgp.utn.edu.ar.quepasa.service.impl;


import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    @Override
    public User update(String username, User newUser) {
        User user = findByUsername(username);
        user.setName(newUser.getName());
        /** @see AuthenticationServiceImpl **/
        // user.setEmail(newUser.getEmail());
        user.setAddress(newUser.getAddress());
        /** @see AuthenticationServiceImpl **/
        // user.setPhone(newUser.getPhone());
        // user.setPassword(newUser.getPassword());
        user.setRole(newUser.getRole());
        if(newUser.getNeighbourhood() != null)
            user.setNeighbourhood(newUser.getNeighbourhood());
        if(newUser.getProfilePicture() != null)
            user.setProfilePicture(newUser.getProfilePicture());
        return userRepository.save(user);
    }

    @Override
    public void delete(String username) {
        User user = findByUsername(username);
        user.setActive(false);
        userRepository.save(user);
    }
}
