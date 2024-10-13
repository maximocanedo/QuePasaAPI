package frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.regex.Pattern;

public class SubnationalDivisionLabelValidatorBuilder extends ValidatorBuilder<String> {

    public SubnationalDivisionLabelValidatorBuilder(String value) {
        super(value.trim(), "label");
    }

    public SubnationalDivisionLabelValidatorBuilder isNotNullOrEmpty() {
        if(getValue() != null && getValue().isBlank()) {
            super.invalidate("El valor ingresado no es válido. ");
        }
        return this;
    }

    public SubnationalDivisionLabelValidatorBuilder hasValidLength() {
        var str = getValue();
        if(str.length() < 3) {
            super.invalidate("El nombre de una entidad subnacional debe tener al menos tres caracteres. ");
        } else if(str.length() > 50) {
            super.invalidate("El nombre de una entidad subnacional debe tener como máximo cincuenta caracteres. ");
        }
        return this;
    }

    public SubnationalDivisionLabelValidatorBuilder isValidLabel() {
        var pattern = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$");
        var matcher = pattern.matcher(getValue());
        if(!matcher.matches()) {
            super.invalidate("Nombre no válido. ");
        }
        return this;
    }



}
