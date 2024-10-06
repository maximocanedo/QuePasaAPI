package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidatorBuilder extends ValidatorBuilder<String> {

    public UsernameValidatorBuilder(String value) {
        super(value, "username");
    }

    public UsernameValidatorBuilder meetsMinimumLength() {
        if(getValue().length() < 4)
            super.invalidate("Debe tener al menos cuatro caracteres. ");
        return this;
    }

    public UsernameValidatorBuilder meetsMaximumLength() {
        if(getValue().length() > 24)
            super.invalidate("No debe tener más de 24 caracteres. ");
        return this;
    }

    public UsernameValidatorBuilder doesntHaveIllegalCharacters() {
        Pattern p = Pattern.compile("^[a-zA-Z0-9._]+$");
        Matcher m = p.matcher(getValue());
        if(!m.matches()) super.invalidate("Debe tener únicamente letras, números, guiones bajos y/o puntos. ");
        return this;
    }

    public UsernameValidatorBuilder neitherStartsNorEndsWithDoubleDotsOrUnderscores() {
        Pattern p = Pattern.compile("^(?![_.]).*(?<![_.])$");
        Matcher m = p.matcher(getValue());
        if(!m.matches()) super.invalidate("Debe tener únicamente letras, números, guiones bajos y/o puntos. ");
        return this;
    }

    public UsernameValidatorBuilder doesntHaveTwoDotsOrUnderscoresInARow() {
        Pattern p = Pattern.compile("^(?!.*[._]{2}).*$");
        Matcher m = p.matcher(getValue());
        if(!m.matches()) super.invalidate("No debe tener dos puntos o guiones bajos seguidos. ");
        return this;
    }

    public UsernameValidatorBuilder isAvailable(UserRepository repository) {
        var exists = repository.findByUsername(getValue()).isPresent();
        if(exists) super.invalidate("Este nombre de usuario no está disponible. ");
        return this;
    }

}
