package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventDescriptionValidator extends ValidatorBuilder<EventDescriptionValidator, String> {
    public EventDescriptionValidator(String value) {
        super(value, "description");
    }

    public EventDescriptionValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Description of the event cannot be null.");
        }
        return this;
    }

    public EventDescriptionValidator isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Description of the event cannot be empty.");
        }
        return this;
    }

    public EventDescriptionValidator isNotTooLong() {
        if (getValue().length() > 500) {
            super.invalidate("Description of the event cannot be longer than 500 characters.");
        }
        return this;
    }
}
