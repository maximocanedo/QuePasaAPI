package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class AuthorizationController {
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }

    @RequestMapping("/api/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                User userDetails = (User) principal;
                return ResponseEntity.ok(userDetails);
            }
            return ResponseEntity.ok(principal);
        }
        return ResponseEntity.badRequest().body("");
    }

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("¡Hola, " + getCurrentUsername() + "!");
    }
}
