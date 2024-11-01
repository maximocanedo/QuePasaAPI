package quepasa.api.validators.geo.subnationaldivision;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class SubnationalDivisionLabelValidator extends StringValidatorBuilder<SubnationalDivisionLabelValidator> {

    public SubnationalDivisionLabelValidator(String value) {
        super(value, "label");
    }

    public SubnationalDivisionLabelValidator isNotNullOrEmpty() {
        if(getValue() == null || getValue().isBlank()) {
            super.invalidate("El valor ingresado no es válido. ");
        }
        if(getValue() != null) { setValue(getValue().trim()); }
        return this;
    }

    public SubnationalDivisionLabelValidator hasValidLength() {
        return super.meetsLimits(
            3,
            50,
            "El nombre de una entidad subnacional debe tener al menos tres caracteres. ",
            "El nombre de una entidad subnacional debe tener como máximo cincuenta caracteres. "
        );
    }

    public SubnationalDivisionLabelValidator isValidLabel() {
        return super.matches(
            "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$",
            "Nombre no válido. "
        );
    }



}
