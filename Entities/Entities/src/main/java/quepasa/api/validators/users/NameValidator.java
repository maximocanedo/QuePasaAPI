package quepasa.api.validators.users;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class NameValidator extends StringValidatorBuilder<NameValidator> {

    public NameValidator(String value) {
        super(value, "name");
        this.setValue(value.trim());
    }

    public NameValidator validateCompoundNames() {
        return super.matches(
            "^(?![\\d\\s\\W])[\\w\\s\\W]+(?<![\\d\\s\\W])$",
            "El nombre no puede empezar con espacios, números o símbolos especiales. "
        );
    }

}
