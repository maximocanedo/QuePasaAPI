package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class PasswordValidator extends ValidatorBuilder<PasswordValidator, String> {

    public PasswordValidator(String value) {
        super(value, "password");
    }

    /**
     * Valida que tenga al menos ocho caracteres.
     */
    public PasswordValidator lengthIsEightCharactersOrMore() {
        if (getValue() == null || getValue().length() <= 8) {
            super.invalidate("Debe tener al menos ocho caracteres.");
        }
        return this;
    }

    /**
     * Valida que tenga al menos una letra mayúscula.
     */
    public PasswordValidator hasOneUpperCaseLetter() {
        boolean hasOneUpperCaseLetter = false;
        for(char c: getValue().toCharArray()) {
            if(Character.isUpperCase(c)) hasOneUpperCaseLetter = true;
        }
        if(!hasOneUpperCaseLetter)
            super.invalidate("Debe tener al menos una letra mayúscula. ");
        return this;
    }

    /**
     * Valida que tenga al menos una letra minúscula
     */
    public PasswordValidator hasOneLowerCaseLetter() {
        boolean hasOneLowerCaseLetter = false;
        for(char c: getValue().toCharArray()) {
            if(Character.isLowerCase(c)) hasOneLowerCaseLetter = true;
        }
        if(!hasOneLowerCaseLetter)
            super.invalidate("Debe tener al menos una letra mayúscula. ");
        return this;
    }

    /**
     * Valida que tenga al menos un dígito.
     */
    public PasswordValidator hasOneDigit() {
        boolean hasOneDigit = false;
        for(char c: getValue().toCharArray()) {
            if(Character.isDigit(c)) hasOneDigit = true;
        }
        if(!hasOneDigit)
            super.invalidate("Debe tener al menos un dígito. ");
        return this;
    }

    /**
     * Valida que tenga al menos un caracter especial.
     */
    public PasswordValidator hasOneSpecialCharacter() {
        boolean hasOneSpecialCharacter = false;
        for(char c: getValue().toCharArray()) {
            if(!Character.isLetterOrDigit(c)) hasOneSpecialCharacter = true;
        }
        if(!hasOneSpecialCharacter)
            super.invalidate("Debe tener al menos un símbolo especial. ");
        return this;
    }

}
