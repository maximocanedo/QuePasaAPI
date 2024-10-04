package frgp.utn.edu.ar.quepasa.service;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, boolean isPartial);

    boolean isTokenValid(String token, UserDetails userService);

    boolean isTokenPartiallyValid(String token, UserDetails user);
}
