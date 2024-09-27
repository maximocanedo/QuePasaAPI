package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // Actual User Methods
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                User currentUser = userService.findByUsername(((UserDetails) principal).getUsername());
                return ResponseEntity.ok(currentUser);
            }
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                User currentUser = userService.findByUsername(((UserDetails) principal).getUsername());
                return ResponseEntity.ok(userService.updateUser(currentUser.getId(), user));
            }
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                User currentUser = userService.findByUsername(((UserDetails) principal).getUsername());
                userService.deleteUser(currentUser.getId());
                return ResponseEntity.ok(HttpStatus.NO_CONTENT);
            }
        }
        return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
    }
}
