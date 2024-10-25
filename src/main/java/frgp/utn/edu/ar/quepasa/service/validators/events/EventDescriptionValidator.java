package frgp.utn.edu.ar.quepasa.service.validators.events;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;

public class EventDescriptionValidator extends StringValidatorBuilder<EventDescriptionValidator> {

    public EventDescriptionValidator(String value) {
        super(value, "description");
    }

    @Deprecated(forRemoval = true)
    public EventDescriptionValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("La descrip");
        }
        return this;
    }

    @Deprecated(forRemoval = true)
    public EventDescriptionValidator isNotEmpty() {
        if (getValue().isEmpty()) {
            super.invalidate("Description of the event cannot be empty.");
        }
        return this;
    }

    public EventDescriptionValidator meetsLimits() {
        return super.meetsLimits(
                1,
                500,
                "La descripción no puede estar vacía. ",
                "La descripción no puede pasar de los quinientos caracteres. "
        );
    }
}
