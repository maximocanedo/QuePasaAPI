package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.time.LocalDateTime;

@Deprecated
public class EventDateValidator extends ValidatorBuilder<EventDateValidator, LocalDateTime> {
    public EventDateValidator(LocalDateTime value) {
        super(value, "start");
    }

    public EventDateValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Date of the event cannot be null.");
        }
        return this;
    }

    public EventDateValidator isNotPast() {
        if (getValue().isBefore(LocalDateTime.now())) {
            super.invalidate("Date of the event cannot be in the past.");
        }
        return this;
    }

    public EventDateValidator isNotBefore(LocalDateTime date) {
        if (getValue().isBefore(date)) {
            super.invalidate("Date of the event cannot be before the current date.");
        }
        return this;
    }
}
