package frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision;

import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubnationalDivisionISO3Validator extends ValidatorBuilder<SubnationalDivisionISO3Validator, String> {

    public SubnationalDivisionISO3Validator(String value) {
        super(value, "iso3");
    }

    public SubnationalDivisionISO3Validator isNotNullOrEmpty() {
        if(getValue() == null || getValue().isBlank()) {
            super.invalidate("El valor ingresado no es válido y no corresponde a un código ISO 3166-2. ");
        }
        return this;
    }

    public SubnationalDivisionISO3Validator isAvailable(SubnationalDivisionRepository repository) {
        if(repository.existsByIso3(getValue())) {
            super.invalidate("El código ISO 3166-2 '" + getValue() + "' no está disponible. ");
        }
        return this;
    }

    public SubnationalDivisionISO3Validator isValidISO31662() {
        Pattern pattern = Pattern.compile("^[A-Z]{2}-[A-Z0-9]{1,3}$");
        Matcher matcher = pattern.matcher(getValue());
        if(!matcher.matches()) {
            super.invalidate("El texto ingresado no corresponde a un código ISO 3166-2. ");
        }
        return this;
    }

}
