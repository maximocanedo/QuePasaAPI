package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

public class OwnerValidatorBuilder {

    private final Ownable object;
    private boolean result;
    private String message;
    private User current = null;

    public OwnerValidatorBuilder(@NotNull Ownable object, @NotNull User current) {
        this.object = object;
        this.current = current;
        this.result = true;
        this.message = "";
    }
    public static OwnerValidatorBuilder create(Ownable object, User currentUser) {
        return new OwnerValidatorBuilder(object, currentUser);
    }

    private User getCurrentUser() {
        /**Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         var user = (UserDetails) authentication.getPrincipal();
         return user;**/
        return current;
    }

    private void letWith(Role role) {
        User user = getCurrentUser();
        if(user.getRole() != null) {
            result = result && (
                    user.getRole().name().equalsIgnoreCase(role.name())
            );
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var details = (UserDetails) authentication.getPrincipal();
        if(details.getAuthorities() != null && !details.getAuthorities().isEmpty()) {
            result = result && (
                    details.getAuthorities().contains(new SimpleGrantedAuthority(role.name()))
                    ||
                    details.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
        }
    }


    public OwnerValidatorBuilder isOwner() {
        var user = getCurrentUser();
        result = result || Objects.equals(object.getOwner().getUsername(), user.getUsername());
        if(!result) message += "No es dueño del registro. ";
        return this;
    }

    public OwnerValidatorBuilder isAdmin() {
        letWith(Role.ADMIN);
        if(!result) message += "No es administrador. ";
        return this;
    }

    public OwnerValidatorBuilder isModerator() {
        letWith(Role.MOD);
        if(!result) message += "No es moderador. ";
        return this;
    }

    public OwnerValidatorBuilder isGovernment() {
        letWith(Role.GOVT);
        if(!result) message += "No es entidad gubernamental. ";
        return this;
    }

    public OwnerValidatorBuilder isOrganization() {
        letWith(Role.ORGANIZATION);
        if(!result) message += "No es organización. ";
        return this;
    }

    public OwnerValidatorBuilder isContributor() {
        letWith(Role.CONTRIBUTOR);
        if(!result) message += "No es contribuidor. ";
        return this;
    }


    public OwnerValidatorBuilder isNeighbour() {
        letWith(Role.NEIGHBOUR);
        if(!result) message += "No es vecino autenticado. ";
        return this;
    }


    public OwnerValidatorBuilder isUser() {
        letWith(Role.USER);
        if(!result) message += "No es usuario. ";
        return this;
    }

    public boolean build() {
        return result;
    }

    public void orElseFail() {
        if (!result) {
            throw new Fail("Error de accesos. Detalle: " + message, HttpStatus.FORBIDDEN);
        }
    }

}
