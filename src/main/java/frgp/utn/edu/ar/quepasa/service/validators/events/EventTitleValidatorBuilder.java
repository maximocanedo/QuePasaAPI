package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventTitleValidatorBuilder extends ValidatorBuilder<String> {
    public EventTitleValidatorBuilder(String value) {
        super(value, "title");
    }

    public EventTitleValidatorBuilder isNotNull() {
        if (getValue() == null) {
            super.invalidate("Title of the event cannot be null.");
        }
        return this;
    }

    public EventTitleValidatorBuilder isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Title of the event cannot be empty.");
        }
        return this;
    }

    public EventTitleValidatorBuilder isNotTooLong() {
        if (getValue().length() > 100) {
            super.invalidate("Title of the event cannot be longer than 100 characters.");
        }
        return this;
    }
}
