package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

@Deprecated
public class EventAddressValidator extends ValidatorBuilder<EventAddressValidator, String> {
    public EventAddressValidator(String value) {
        super(value, "address");
    }

    public EventAddressValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Address of the event cannot be null.");
        }
        return this;
    }

    public EventAddressValidator isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Address of the event cannot be empty.");
        }
        return this;
    }

    public EventAddressValidator isNotTooLong() {
        if (getValue().length() > 100) {
            super.invalidate("Address of the event cannot be longer than 100 characters.");
        }
        return this;
    }
}
