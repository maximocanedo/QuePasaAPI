package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventTitleValidator extends ValidatorBuilder<EventTitleValidator, String> {
    public EventTitleValidator(String value) {
        super(value, "title");
    }

    public EventTitleValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Title of the event cannot be null.");
        }
        return this;
    }

    public EventTitleValidator isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Title of the event cannot be empty.");
        }
        return this;
    }

    public EventTitleValidator isNotTooLong() {
        if (getValue().length() > 100) {
            super.invalidate("Title of the event cannot be longer than 100 characters.");
        }
        return this;
    }
}
