package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailValidator extends ValidatorBuilder<MailValidator, String> {

    public MailValidator(String value) {
        super(value, "mail");
        setValue(value.trim());
    }

    public MailValidator isValidAddress() {
        Pattern p = Pattern.compile(".+@.+\\..+");
        Matcher m = p.matcher(getValue());
        if(!m.matches())
            super.invalidate("Debe ser una dirección de correo electrónico válida. ");
        return this;
    }

}
