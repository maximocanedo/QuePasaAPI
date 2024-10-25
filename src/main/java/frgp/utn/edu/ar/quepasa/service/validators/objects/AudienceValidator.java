package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.ValidatorBuilder;

public class AudienceValidator extends ValidatorBuilder<AudienceValidator, Audience> {

    public AudienceValidator(Audience value) {
        super(value, "audience");
    }

    public AudienceValidator isNotNull() {
        if (getValue() == null) {
            super.invalidate("Este campo no puede estar nulo. ");
        }
        return this;
    }

}
