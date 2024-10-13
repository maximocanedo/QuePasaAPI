package frgp.utn.edu.ar.quepasa.service.validators.geo.subnationaldivision;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.regex.Pattern;

public class SubnationalDivisionLabelValidatorBuilder extends ValidatorBuilder<String> {

    public SubnationalDivisionLabelValidatorBuilder(String value) {
        super(value, "label");
    }

    public SubnationalDivisionLabelValidatorBuilder isNotNullOrEmpty() {
        if(getValue() == null || getValue().isBlank()) {
            super.invalidate("El valor ingresado no es válido. ");
        }
        if(getValue() != null) { setValue(getValue().trim()); }
        return this;
    }

    public SubnationalDivisionLabelValidatorBuilder hasValidLength() {
        this.isNotNullOrEmpty();
        var str = getValue();
        if(str.length() < 3) {
            super.invalidate("El nombre de una entidad subnacional debe tener al menos tres caracteres. ");
        } else if(str.length() > 50) {
            super.invalidate("El nombre de una entidad subnacional debe tener como máximo cincuenta caracteres. ");
        }
        return this;
    }

    public SubnationalDivisionLabelValidatorBuilder isValidLabel() {
        this.isNotNullOrEmpty();
        var pattern = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$");
        var matcher = pattern.matcher(getValue());
        if(!matcher.matches()) {
            super.invalidate("Nombre no válido. ");
        }
        return this;
    }



}
