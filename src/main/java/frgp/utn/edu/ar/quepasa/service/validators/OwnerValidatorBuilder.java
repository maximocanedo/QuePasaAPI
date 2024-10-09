package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public class OwnerValidatorBuilder {

    @NotNull
    private final AuthenticationService authenticationService;
    private final Ownable object;
    private boolean result;
    private String message;

    public OwnerValidatorBuilder(@NotNull Ownable object, @NotNull AuthenticationService authenticationService) {
        this.object = object;
        this.authenticationService = authenticationService;
        this.result = true;
        this.message = "";
    }

    public OwnerValidatorBuilder isOwner() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && Objects.equals(object.getOwner().getUsername(), user.getUsername());
        if(!result) message += "No es dueño del registro. ";
        return this;
    }

    public OwnerValidatorBuilder isAdmin() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && (user.getRole() == Role.ADMIN);
        if(!result) message += "No es administrador. ";
        return this;
    }

    public OwnerValidatorBuilder isModerator() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && (user.getRole() == Role.MOD);
        if(!result) message += "No es moderador. ";
        return this;
    }

    public OwnerValidatorBuilder isGovernment() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && (user.getRole() == Role.GOVT);
        if(!result) message += "No es entidad gubernamental. ";
        return this;
    }

    public OwnerValidatorBuilder isOrganization() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && (user.getRole() == Role.ORGANIZATION);
        if(!result) message += "No es organización. ";
        return this;
    }

    public OwnerValidatorBuilder isContributor() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && (user.getRole() == Role.CONTRIBUTOR);
        if(!result) message += "No es contribuidor. ";
        return this;
    }


    public OwnerValidatorBuilder isUser() {
        var user = authenticationService.getCurrentUserOrDie();
        result = result && (user.getRole() == Role.USER);
        if(!result) message += "No es usuario. ";
        return this;
    }

    public OwnerValidatorBuilder and() {
        return this;
    }

    public OwnerValidatorBuilder or() {
        this.result = false;
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
