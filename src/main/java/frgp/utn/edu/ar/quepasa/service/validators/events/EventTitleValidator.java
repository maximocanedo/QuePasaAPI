package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;

public class EventTitleValidator extends StringValidatorBuilder<EventTitleValidator> {
    public EventTitleValidator(String value) {
        super(value, "title");
    }

    @Deprecated(forRemoval = true)
    public EventTitleValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Title of the event cannot be null.");
        }
        return this;
    }

    @Deprecated(forRemoval = true)
    public EventTitleValidator isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Title of the event cannot be empty.");
        }
        return this;
    }

    public EventTitleValidator meetsLimits() {
        return super.meetsLimits(
                1,
                100,
                "El título no puede estar vacío. ",
                "El título no puede pasar de los cien caracteres. "
        );
    }
}
