package quepasa.api.validators.users;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class NameValidator extends StringValidatorBuilder<NameValidator> {

    public NameValidator(String value) {
        super(value, "name");
        this.setValue(value.trim());
    }

    public NameValidator validateCompoundNames() {
        return super.matches(
            "^[A-Za-zÁÉÍÓÚáéíóúñÑ'’-]{2,}( [A-Za-zÁÉÍÓÚáéíóúñÑ'’-]{2,})*$",
            "Cada nombre debe tener al menos dos caracteres válidos. "
        );
    }

}
