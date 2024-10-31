package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import quepasa.api.validators.commons.builders.ValidatorBuilder;

public final class EventCategoryValidator extends ValidatorBuilder<EventCategoryValidator, EventCategory> {

    public EventCategoryValidator(EventCategory value) {
        super(value, "category");
    }

    public EventCategoryValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("La categoría del evento no puede ser nula. ");
        }
        return this;
    }

}
