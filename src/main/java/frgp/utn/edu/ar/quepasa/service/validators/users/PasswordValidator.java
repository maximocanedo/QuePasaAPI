package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;

public class PasswordValidator extends StringValidatorBuilder<PasswordValidator> {

    public PasswordValidator(String value) {
        super(value, "password");
    }

    /**
     * Valida que tenga al menos ocho caracteres.
     */
    public PasswordValidator lengthIsEightCharactersOrMore() {
        return super.hasMinimumLength(9, "Debe tener al menos ocho caracteres.");
    }

    /**
     * Valida que tenga al menos una letra mayúscula.
     */
    public PasswordValidator hasOneUpperCaseLetter() {
        return super.hasAtLeastXUpperCaseLetters(1, "Debe tener al menos una letra mayúscula. ");
    }

    /**
     * Valida que tenga al menos una letra minúscula
     */
    public PasswordValidator hasOneLowerCaseLetter() {
        return super.hasAtLeastXLowerCaseLetters(1, "Debe tener al menos una letra minúscula. ");
    }

    /**
     * Valida que tenga al menos un dígito.
     */
    public PasswordValidator hasOneDigit() {
        return super.hasAtLeastXDigits(1, "Debe tener al menos un dígito. ");
    }

    /**
     * Valida que tenga al menos un caracter especial.
     */
    public PasswordValidator hasOneSpecialCharacter() {
        return super.hasAtLeastXSpecialCharacters(1, "Debe tener al menos un símbolo especial. ");
    }

}
