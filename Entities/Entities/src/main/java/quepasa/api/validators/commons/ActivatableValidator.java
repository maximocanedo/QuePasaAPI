package quepasa.api.validators.commons;

import quepasa.api.entities.Activatable;
import quepasa.api.validators.commons.builders.ActivatableValidatorBuilder;

public class ActivatableValidator extends ActivatableValidatorBuilder<ActivatableValidator> {
    public ActivatableValidator(Activatable value, String fieldName) {
        super(value, fieldName);
    }
    public ActivatableValidator(Activatable value) {
        super(value);
    }
}
