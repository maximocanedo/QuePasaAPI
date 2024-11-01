package quepasa.api.validators.commons.builders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Clase validadora builder de cadenas de texto. </p>
 * <b>No validar directamente con esta clase, se debe heredar de esta clase o usar {@link quepasa.api.validators.commons.StringValidator StringValidator}.</b>
 */
@SuppressWarnings("unchecked")
public abstract class StringValidatorBuilder<T extends StringValidatorBuilder<T>> extends ValidatorBuilder<T, String> {

    public StringValidatorBuilder(String value, String fieldName) {
        super(value, fieldName);
    }
    public StringValidatorBuilder(String value) {
        this(value, "unknown");
    }

    public T trim() {
        this.isNotNull();
        setValue(getValue().trim());
        return (T) this;
    }
    public T ifNullThen(String newValue) {
        if(getValue() == null) setValue(newValue);
        return (T) this;
    }
    public T ifBlankThen(String newValue) {
        if(getValue().isBlank()) setValue(newValue);
        return (T) this;
    }
    public T ifNullOrBlankThen(String newValue) {
        return this
                .ifNullThen(newValue)
                .ifBlankThen(newValue);
    }

    public T isNotNull(String feedback) {
        if(getValue() == null)
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNotNull() {
        return this.isNotNull("El valor no debe ser nulo. ");
    }

    public T hasMinimumLength(int minLength, String feedback) {
        if(getValue().length() < minLength)
            super.invalidate(feedback);
        return (T) this;
    }
    public T hasMinimumLength(int minLength) {
        return this.hasMinimumLength(minLength, "Debe tener mínimo " + minLength + " caracteres. ");
    }

    public T hasMaximumLength(int maxLength, String feedback) {
        if(getValue().length() > maxLength)
            super.invalidate(feedback);
        return (T) this;
    }
    public T hasMaximumLength(int maxLength) {
        return this.hasMaximumLength(maxLength, "Debe tener hasta " + maxLength + " caracteres. ");
    }

    public T meetsLimits(int min, int max, String minCaseFeedback, String maxCaseFeedback) {
        return this
                .isNotNull()
                .hasMinimumLength(min, minCaseFeedback)
                .hasMaximumLength(max, maxCaseFeedback);
    }
    public T meetsLimits(int min, int max, String feedback) {
        return this
                .isNotNull()
                .hasMinimumLength(min, feedback)
                .hasMaximumLength(max, feedback);
    }
    public T meetsLimits(int min, int max) {
        return this
                .isNotNull()
                .hasMinimumLength(min)
                .hasMaximumLength(max);
    }

    public T isNotBlank(String feedback) {
        if(getValue().trim().isBlank())
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNotBlank() {
        return this.isNotBlank("No debe estar vacío. ");
    }

    public T matches(String regex, String feedback) {
        this.isNotNull();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(getValue());
        if(!matcher.matches())
            super.invalidate(feedback);
        return (T) this;
    }

    private int countUpperCaseLetters() {
        int count = 0;
        for(char c: getValue().toCharArray()) {
            if(Character.isUpperCase(c)) count++;
        }
        return count;
    }
    private int countLowerCaseLetters() {
        int count = 0;
        for(char c: getValue().toCharArray()) {
            if(Character.isLowerCase(c)) count++;
        }
        return count;
    }

    public T hasXUpperCaseLetters(int x, String feedback) {
        if(x != countUpperCaseLetters())
            super.invalidate(feedback);
        return (T) this;
    }
    public T hasXUpperCaseLetters(int x) {
        return this.hasXUpperCaseLetters(x, "Debe tener " + x + " mayúsculas. ");
    }

    public T hasXLowerCaseLetters(int x, String feedback) {
        if(x != countLowerCaseLetters())
            super.invalidate(feedback);
        return (T) this;
    }
    public T hasXLowerCaseLetters(int x) {
        return this.hasXLowerCaseLetters(x, "Debe tener " + x + " minúsculas. ");
    }

    public T hasAtLeastXUpperCaseLetters(int min, String feedback) {
        if(countUpperCaseLetters() < min) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtLeastXUpperCaseLetters(int min) {
        return this.hasAtLeastXUpperCaseLetters(min, "Debe tener al menos " + min + " mayúsculas. ");
    }

    public T hasAtLeastXLowerCaseLetters(int min, String feedback) {
        if(countLowerCaseLetters() < min) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtLeastXLowerCaseLetters(int min) {
        return this.hasAtLeastXLowerCaseLetters(min, "Debe tener al menos " + min + " mayúsculas. ");
    }

    public T hasAtMostXUpperCaseLetters(int max, String feedback) {
        if(countUpperCaseLetters() > max) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtMostXUpperCaseLetters(int max) {
        return this.hasAtMostXUpperCaseLetters(max, "No debe tener más de " + max + " mayúsculas. ");
    }

    public T hasAtMostXLowerCaseLetters(int max, String feedback) {
        if(countLowerCaseLetters() > max) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtMostXLowerCaseLetters(int max) {
        return this.hasAtMostXLowerCaseLetters(max, "No debe tener más de " + max + " mayúsculas. ");
    }

    private int countDigits() {
        int count = 0;
        for(char c: getValue().toCharArray()) {
            if(Character.isDigit(c)) count++;
        }
        return count;
    }

    public T hasXDigits(int x, String feedback) {
        if(x != countDigits())
            super.invalidate(feedback);
        return (T) this;
    }
    public T hasXDigits(int x) {
        return this.hasXDigits(x, "Debe tener " + x + " dígitos. ");
    }

    public T hasAtLeastXDigits(int min, String feedback) {
        if(countDigits() < min) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtLeastXDigits(int min) {
        return this.hasAtLeastXDigits(min, "Debe tener al menos " + min + " dígitos. ");
    }

    public T hasAtMostXDigits(int max, String feedback) {
        if(countDigits() > max) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtMostXDigits(int max) {
        return this.hasAtMostXDigits(max, "No debe tener más de " + max + " dígitos. ");
    }

    private int countSpecialCharacters() {
        int count = 0;
        for(char c: getValue().toCharArray()) {
            if(!Character.isLetterOrDigit(c)) count++;
        }
        return count;
    }

    public T hasXSpecialCharacters(int x, String feedback) {
        if(x != countSpecialCharacters())
            super.invalidate(feedback);
        return (T) this;
    }
    public T hasXSpecialCharacters(int x) {
        return this.hasXSpecialCharacters(x, "Debe tener " + x + " caracteres especiales. ");
    }

    public T hasAtLeastXSpecialCharacters(int min, String feedback) {
        if(countSpecialCharacters() < min) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtLeastXSpecialCharacters(int min) {
        return this.hasAtLeastXSpecialCharacters(min, "Debe tener al menos " + min + " caracteres especiales. ");
    }

    public T hasAtMostXSpecialCharacters(int max, String feedback) {
        if(countSpecialCharacters() > max) super.invalidate(feedback);
        return (T) this;
    }
    public T hasAtMostXSpecialCharacters(int max) {
        return this.hasAtMostXSpecialCharacters(max, "No debe tener más de " + max + " caracteres especiales. ");
    }



}
