package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;

public class MailValidator extends StringValidatorBuilder<MailValidator> {

    public MailValidator(String value) {
        super(value, "mail");
        setValue(value.trim());
    }

    public MailValidator isValidAddress() {
        return super.matches(
            ".+@.+\\..+",
            "Debe ser una dirección de correo electrónico válida. "
        );
    }

}
