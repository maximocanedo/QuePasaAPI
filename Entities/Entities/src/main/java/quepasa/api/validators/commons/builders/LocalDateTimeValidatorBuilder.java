package quepasa.api.validators.commons.builders;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>Clase validadora builder de objetos {@link LocalTime LocalTime}. </p>
 * <b>No validar directamente con esta clase, se debe heredar de esta clase o usar {@link quepasa.api.validators.commons.LocalDateTimeValidator LocalDateTimeValidator}.</b>
 */
@SuppressWarnings("unchecked")
public abstract class LocalDateTimeValidatorBuilder<T extends LocalDateTimeValidatorBuilder<T>> extends ValidatorBuilder<T, LocalDateTime> {

    public LocalDateTimeValidatorBuilder(LocalDateTime value, String fieldName) {
        super(value, fieldName);
    }
    public LocalDateTimeValidatorBuilder(LocalDateTime value) {
        this(value, "localDateTimeField");
    }

    public T isNotNull(String feedback) {
        if(getValue() == null)
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNotNull() {
        return this.isNotNull("El valor no puede ser nulo. ");
    }

    public T isBefore(LocalDateTime time, String feedback) {
        if(!getValue().isBefore(time))
            super.invalidate(feedback);
        return (T) this;
    }
    public T isBefore(LocalDateTime time) {
        return this.isBefore(time, "Debe ser anterior a: " + time.toString());
    }

    public T isNotBefore(LocalDateTime time, String feedback) {
        if(getValue().isBefore(time))
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNotBefore(LocalDateTime time) {
        return this.isNotBefore(time, "Debe ser posterior a: " + time.toString());
    }

    public T hasOccurred(String feedback) {
        return this.isBefore(LocalDateTime.now(), feedback);
    }
    public T hasOccurred() {
        return this.hasOccurred("No debe ser anterior al presente. ");
    }

    public T hasNotOccurred(String feedback) {
        return this.isNotBefore(LocalDateTime.now(), feedback);
    }
    public T hasNotOccurred() {
        return this.hasNotOccurred("No debe ser anterior al presente. ");
    }

    public T isAfter(LocalDateTime time, String feedback) {
        if(!getValue().isBefore(time))
            super.invalidate(feedback);
        return (T) this;
    }
    public T isAfter(LocalDateTime time) {
        return this.isAfter(time, "Debe ser posterior a: " + time.toString());
    }

    public T isNotAfter(LocalDateTime time, String feedback) {
        if(getValue().isBefore(time))
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNotAfter(LocalDateTime time) {
        return this.isNotAfter(time, "Debe ser anterior a: " + time.toString());
    }

}
