package quepasa.api.validators.events;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class EventTitleValidator extends StringValidatorBuilder<EventTitleValidator> {

    public EventTitleValidator(String value) {
        super(value, "title");
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
