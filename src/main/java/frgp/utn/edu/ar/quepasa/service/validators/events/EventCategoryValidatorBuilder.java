package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventCategoryValidatorBuilder extends ValidatorBuilder<EventCategory> {
    public EventCategoryValidatorBuilder(EventCategory value) {
        super(value, "category");
    }

    public EventCategoryValidatorBuilder isNotNull() {
        if (getValue() == null) {
            super.invalidate("Category of the event cannot be null.");
        }
        return this;
    }

    public EventCategoryValidatorBuilder isNotInvalid() {
        try {
            EventCategory.valueOf(getValue().name());
        } catch (IllegalArgumentException e) {
            super.invalidate("Category of the event is invalid.");
        }
        return this;
    }
}
