package frgp.utn.edu.ar.quepasa.service.validators.commons;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;

/**
 * <p>Clase validadora de cadenas de texto.</p>
 * <b>No heredar de esta clase, heredar de {@link StringValidatorBuilder StringValidatorBuilder}</b>
 */
public class StringValidator extends StringValidatorBuilder<StringValidator> {

    public StringValidator(String value, String fieldName) {
        super(value, fieldName);
    }

    public StringValidator(String value) {
        this(value, "unknown");
    }

    /**
     * No añadir métodos a esta clase.
     */

}
