package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

@Deprecated
public class EventAudienceValidator extends ValidatorBuilder<EventAudienceValidator, Audience> {
    public EventAudienceValidator(Audience value) {
        super(value, "audience");
    }

    public EventAudienceValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Audience of the event cannot be null.");
        }
        return this;
    }

    @Deprecated(forRemoval = true)
    public EventAudienceValidator isNotInvalid() {
        try {
            Audience.valueOf(getValue().name());
        } catch (IllegalArgumentException e) {
            super.invalidate("Audience of the event is invalid.");
        }
        return this;
    }
}
