package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventDescriptionValidatorBuilder extends ValidatorBuilder<String> {
    public EventDescriptionValidatorBuilder(String value) {
        super(value, "description");
    }

    public EventDescriptionValidatorBuilder isNotNull() {
        if (getValue() == null) {
            super.invalidate("Description of the event cannot be null.");
        }
        return this;
    }

    public EventDescriptionValidatorBuilder isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Description of the event cannot be empty.");
        }
        return this;
    }

    public EventDescriptionValidatorBuilder isNotTooLong() {
        if (getValue().length() > 500) {
            super.invalidate("Description of the event cannot be longer than 500 characters.");
        }
        return this;
    }
}
