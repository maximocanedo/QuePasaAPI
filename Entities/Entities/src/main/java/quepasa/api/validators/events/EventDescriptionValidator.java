package quepasa.api.validators.events;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class EventDescriptionValidator extends StringValidatorBuilder<EventDescriptionValidator> {

    public EventDescriptionValidator(String value) {
        super(value, "description");
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
