package quepasa.api.validators.commons;

import quepasa.api.validators.commons.builders.ObjectValidatorBuilder;

public class ObjectValidator<T> extends ObjectValidatorBuilder<ObjectValidator<T>, T> {

    public ObjectValidator(T value, String fieldName) { super(value, fieldName); }
    public ObjectValidator(T value) { super(value); }

    /**
     * No añadir métodos a esta clase.
     */

}
