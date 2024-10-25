package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;

public class UsernameValidator extends StringValidatorBuilder<UsernameValidator> {

    public UsernameValidator(String value) {
        super(value, "username");
    }

    public UsernameValidator meetsMinimumLength() {
        return super.hasMinimumLength(4, "Debe tener al menos cuatro caracteres. ");
    }

    public UsernameValidator meetsMaximumLength() {
        return super.hasMaximumLength(24, "No debe tener más de 24 caracteres. ");
    }

    public UsernameValidator doesntHaveIllegalCharacters() {
        return super.matches(
            "^[a-zA-Z0-9._]+$",
            "Debe tener únicamente letras, números, guiones bajos y/o puntos. "
        );
    }

    public UsernameValidator neitherStartsNorEndsWithDoubleDotsOrUnderscores() {
        return super.matches(
            "^(?![_.]).*(?<![_.])$",
            "No puede comenzar o terminar con puntos o guiones bajos. "
        );
    }

    public UsernameValidator doesntHaveTwoDotsOrUnderscoresInARow() {
        return super.matches(
            "^(?!.*[._]{2}).*$",
            "No debe tener dos puntos o guiones bajos seguidos. "
        );
    }

    public UsernameValidator isAvailable(UserRepository repository) {
        var exists = repository.findByUsername(getValue()).isPresent();
        if(exists) super.invalidate("Este nombre de usuario no está disponible. ");
        return this;
    }

}
