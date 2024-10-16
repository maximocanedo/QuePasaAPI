package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventAddressValidatorBuilder extends ValidatorBuilder<String> {
    public EventAddressValidatorBuilder(String value) {
        super(value, "address");
    }

    public EventAddressValidatorBuilder isNotNull() {
        if (getValue() == null) {
            super.invalidate("Address of the event cannot be null.");
        }
        return this;
    }

    public EventAddressValidatorBuilder isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Address of the event cannot be empty.");
        }
        return this;
    }

    public EventAddressValidatorBuilder isNotTooLong() {
        if (getValue().length() > 100) {
            super.invalidate("Address of the event cannot be longer than 100 characters.");
        }
        return this;
    }
}
