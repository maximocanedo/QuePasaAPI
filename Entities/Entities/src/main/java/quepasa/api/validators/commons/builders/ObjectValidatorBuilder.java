package quepasa.api.validators.commons.builders;

@SuppressWarnings("unchecked")
public class ObjectValidatorBuilder<T extends ObjectValidatorBuilder<T, V>, V> extends ValidatorBuilder<T, V> {

    public ObjectValidatorBuilder(V value, String fieldName) {
        super(value, fieldName);
    }

    public ObjectValidatorBuilder(V value) {
        this(value, "unknownObject");
    }

    public T isNull(String feedback) {
        if(getValue() != null)
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNull() {
        return this.isNull("El valor no es nulo. ");
    }

    public T isNotNull(String feedback) {
        if(getValue() == null) super.invalidate(feedback);
        return (T) this;
    }
    public T isNotNull() {
        return this.isNotNull("El valor es nulo. ");
    }

    public T ifNullThen(V valueIfNull) {
        if(getValue() == null) setValue(valueIfNull);
        return (T) this;
    }
    public T ifNotNullThen(V valueIfNotNull) {
        if(getValue() != null) setValue(valueIfNotNull);
        return (T) this;
    }



}
