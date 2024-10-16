package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.time.LocalDateTime;

public class EventDateValidatorBuilder extends ValidatorBuilder<LocalDateTime> {
    public EventDateValidatorBuilder(LocalDateTime value) {
        super(value, "start");
    }

    public EventDateValidatorBuilder isNotNull() {
        if (getValue() == null) {
            super.invalidate("Date of the event cannot be null.");
        }
        return this;
    }

    public EventDateValidatorBuilder isNotPast() {
        if (getValue().isBefore(LocalDateTime.now())) {
            super.invalidate("Date of the event cannot be in the past.");
        }
        return this;
    }

    public EventDateValidatorBuilder isNotBefore(LocalDateTime date) {
        if (getValue().isBefore(date)) {
            super.invalidate("Date of the event cannot be before the current date.");
        }
        return this;
    }
}
