package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.time.LocalDateTime;

public class EventDateValidatorBuilder extends ValidatorBuilder<LocalDateTime> {
    public EventDateValidatorBuilder(LocalDateTime value) {
        super(value, "start");
    }

    public EventDateValidatorBuilder isNotPast() {
        if (getValue().isBefore(LocalDateTime.now())) {
            super.invalidate("La fecha del evento no puede ser pasada. ");
        }
        return this;
    }

    public EventDateValidatorBuilder isNotBefore(LocalDateTime date) {
        if (getValue().isBefore(date)) {
            super.invalidate("La fecha del evento no puede ser antes de la fecha actual. ");
        }
        return this;
    }
}
