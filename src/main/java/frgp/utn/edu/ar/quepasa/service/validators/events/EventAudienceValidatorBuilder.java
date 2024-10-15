package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class EventAudienceValidatorBuilder extends ValidatorBuilder<Audience> {
    public EventAudienceValidatorBuilder(Audience value) {
        super(value, "audience");
    }

    public EventAudienceValidatorBuilder isNotNull() {
        if (getValue() == null) {
            super.invalidate("Audience of the event cannot be null.");
        }
        return this;
    }

    public EventAudienceValidatorBuilder isNotInvalid() {
        try {
            Audience.valueOf(getValue().name());
        } catch (IllegalArgumentException e) {
            super.invalidate("Audience of the event is invalid.");
        }
        return this;
    }
}
