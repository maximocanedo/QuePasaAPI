package quepasa.api.validators.users;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class MailValidator extends StringValidatorBuilder<MailValidator> {

    public MailValidator(String value) {
        super(value, "mail");
    }

    public MailValidator isValidAddress() {
        return super.matches(
            ".+@.+\\..+",
            "Debe ser una dirección de correo electrónico válida. "
        );
    }

}
