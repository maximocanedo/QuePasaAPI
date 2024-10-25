package frgp.utn.edu.ar.quepasa.service.validators.commons;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.LocalDateTimeValidatorBuilder;

import java.time.LocalDateTime;

public final class LocalDateTimeValidator extends LocalDateTimeValidatorBuilder<LocalDateTimeValidator> {
    public LocalDateTimeValidator(LocalDateTime value, String fieldName) {
        super(value, fieldName);
    }
    public LocalDateTimeValidator(LocalDateTime value) {
        this(value, "localDateTimeField");
    }

    /**
     * No añadir métodos a esta clase.
     */

}
