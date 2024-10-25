package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Consumer;

public class OwnerValidatorBuilder {

    private final Ownable object;
    @Deprecated(forRemoval = true)
    private boolean result;
    private final BooleanBuilder builder;
    private String message;
    private User current = null;

    public interface OwnerValidatorConsumer extends Consumer<OwnerValidatorBuilder> { }

    public OwnerValidatorBuilder(@NotNull Ownable object, @NotNull User current) {
        this.object = object;
        this.current = current;
        this.result = false;
        this.builder = new BooleanBuilder(false);
        this.message = "";
    }

    public static OwnerValidatorBuilder create(Ownable object, User currentUser) {
        return new OwnerValidatorBuilder(object, currentUser);
    }

    private User getCurrentUser() {
        return current;
    }

    @Deprecated(forRemoval = true)
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
            result &= (
                    details.getAuthorities().contains(new SimpleGrantedAuthority(role.name())) ||
                    details.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role.name()))
            );
        }
    }

    private boolean hasRole(Role role) {
        User user = getCurrentUser();
        if(user.getRole() != null)
            return user.getRole().name().equalsIgnoreCase(role.name());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var details = (UserDetails) authentication.getPrincipal();
        if(details.getAuthorities() != null && !details.getAuthorities().isEmpty()) {
            return (details.getAuthorities().contains(new SimpleGrantedAuthority(role.name())) || details.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role.name())));
        }
        return false;
    }

    public OwnerValidatorBuilder and(OwnerValidatorConsumer ...expressions) {
        BooleanBuilder tempBuilder = new BooleanBuilder(true);

        for (OwnerValidatorConsumer consumer : expressions) {
            consumer.accept(this);
            if (!builder.build()) {
                tempBuilder.and(false);
                break;
            } else tempBuilder.and(builder.build());
        }
        this.builder.and(tempBuilder.build());
        return this;
    }

    public OwnerValidatorBuilder evaluate(String message) {
        if(!builder.build()) this.message += message;
        return this;
    }

    public OwnerValidatorBuilder isOwner() {
        builder.or(object.getOwner().getUsername().equals(getCurrentUser().getUsername()));
        return evaluate("No es dueño del registro. ");
    }

    public OwnerValidatorBuilder isAdmin() {
        var b = hasRole(Role.ADMIN);
        builder.or(b);
        return evaluate("No es administrador. ");
    }

    public OwnerValidatorBuilder isModerator() {
        builder.or(hasRole(Role.MOD));
        return evaluate("No es moderador. ");
    }

    public OwnerValidatorBuilder isGovernment() {
        builder.or(hasRole(Role.GOVT));
        return evaluate("No es entidad gubernamental. ");
    }

    public OwnerValidatorBuilder isOrganization() {
        builder.or(hasRole(Role.ORGANIZATION));
        return evaluate("No es organización. ");
    }

    public OwnerValidatorBuilder isContributor() {
        builder.or(hasRole(Role.CONTRIBUTOR));
        return evaluate("No es contribuidor. ");
    }


    public OwnerValidatorBuilder isNeighbour() {
        builder.or(hasRole(Role.NEIGHBOUR));
        return evaluate("No es vecino autenticado. ");
    }


    public OwnerValidatorBuilder isUser() {
        builder.or(hasRole(Role.USER));
        return evaluate("No es usuario. ");
    }

    public boolean build() {
        return builder.build();
    }

    public void orElseFail() {
        if (!builder.build()) {
            throw new Fail("Error de accesos. Detalle: " + message, HttpStatus.FORBIDDEN);
        }
    }

}
