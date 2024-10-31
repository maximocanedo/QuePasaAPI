package quepasa.api.validators.geo.subnationaldivision;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;
import quepasa.api.verifiers.geo.CountryVerifier;

public class CountryIso3Validator extends StringValidatorBuilder<CountryIso3Validator> {
    public CountryIso3Validator(String value) {
        super(value, "countryIso3");
    }
    public CountryIso3Validator exists(CountryVerifier verifier) {
        if(getValue() == null) {
            super.invalidate("El código ISO3 no puede ser nulo. ");
        }
        if(!verifier.existsByIso3(getValue())) {
            super.invalidate("El país al que se hace referencia no existe. ");
        }
        return this;
    }
}
