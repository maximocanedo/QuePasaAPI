package frgp.utn.edu.ar.quepasa.service.validators.events;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public final class EventAddressValidator extends StringValidatorBuilder<EventAddressValidator> {

    public EventAddressValidator(String value) {
        super(value, "address");
    }

    public EventAddressValidator meetsLimits() {
        return super.meetsLimits(
                1,
                100,
                "La dirección del evento no puede estar vacía. ",
                "La dirección del evento no puede ser mayor a cien caracteres. ");
    }

}
