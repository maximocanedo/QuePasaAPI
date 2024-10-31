package quepasa.api.validators.geo.subnationaldivision;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;
import quepasa.api.verifiers.geo.SubnationalDivisionVerifier;

public class SubnationalDivisionISO3Validator extends StringValidatorBuilder<SubnationalDivisionISO3Validator> {

    public SubnationalDivisionISO3Validator(String value) {
        super(value, "iso3");
    }

    public SubnationalDivisionISO3Validator isNotNullOrEmpty() {
        return super
                .isNotNull("El valor recibido es nulo. ")
                .isNotBlank("El código ISO 3166-2 no puede estar vacío. ");
    }

    public SubnationalDivisionISO3Validator isAvailable(SubnationalDivisionVerifier verifier) {
        if(verifier.existsByIso3(getValue())) {
            super.invalidate("El código ISO 3166-2 '" + getValue() + "' no está disponible. ");
        }
        return this;
    }

    public SubnationalDivisionISO3Validator isValidISO31662() {
        return super.matches(
            "^[A-Z]{2}-[A-Z0-9]{1,3}$",
            "El texto ingresado no corresponde a un código ISO 3166-2. "
        );
    }

}
