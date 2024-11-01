package frgp.utn.edu.ar.quepasa.controller;

import quepasa.api.exceptions.ValidationError;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated(forRemoval = true)
@RestController
@RequestMapping("/api/test")
public class AuthorizationController {

    /**
     * Devuelve el username del usuario actualmente autenticado, o null si no hay
     * @return el username del usuario actualmente autenticado, o null si no hay
     */
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

    /**
     * Devuelve el usuario autenticado actual.
     *
     * Si el usuario no est  autenticado, devuelve un estado de error.
     *
     * @return el usuario autenticado actual
     */
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

    /**
     * Saluda al usuario autenticado actual.
     *
     * Si el usuario no est  autenticado, devuelve un estado de error.
     *
     * @return un saludo con el nombre del usuario autenticado actual
     */
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("¡Hola, " + getCurrentUsername() + "!");
    }

}
