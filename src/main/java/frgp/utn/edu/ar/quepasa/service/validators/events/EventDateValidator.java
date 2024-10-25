package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.LocalDateTimeValidatorBuilder;

import java.time.LocalDateTime;

public final class EventDateValidator extends LocalDateTimeValidatorBuilder<EventDateValidator> {
    public EventDateValidator(LocalDateTime value) {
        super(value, "start");
    }

    public EventDateValidator isNotNull() {
        return super.isNotNull("La fecha del evento no puede ser nula. ");
    }

    public EventDateValidator isNotPast() {
        return super.hasNotOccurred("La fecha del evento no puede ser anterior al presente. ");
    }

    public EventDateValidator isAfterStartDate(LocalDateTime date) {
        return super.isNotBefore(date, "La fecha del evento no puede ser anterior a la fecha de inicio. ");
    }
}