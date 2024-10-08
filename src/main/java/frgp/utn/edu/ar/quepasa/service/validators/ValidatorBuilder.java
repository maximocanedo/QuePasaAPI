package frgp.utn.edu.ar.quepasa.service.validators;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ValidatorBuilder<T> {

    public enum OnInvalidateAction {
        THROW_EXCEPTION,
        THROW_EXCEPTION_ON_BUILD,
        DO_NOTHING
    }

    public OnInvalidateAction onInvalidateAction = OnInvalidateAction.THROW_EXCEPTION;

    public static class ValidationErrorSerializer extends JsonSerializer<ValidationError> {
        @Override
        public void serialize(ValidationError value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("field", value.getField());
            gen.writeArrayFieldStart("errors");
            for (String error : value.getErrors()) {
                gen.writeString(error);
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    public static class ValidationErrorDeserializer extends JsonDeserializer<ValidationError> {
        @Override
        public ValidationError deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            String field = node.get("field").asText();
            Set<String> errors = new HashSet<>();
            node.get("errors").forEach(errorNode -> errors.add(errorNode.asText()));
            return new ValidationError(field, errors);
        }
    }

    @JsonSerialize(using = ValidationErrorSerializer.class)
    @JsonDeserialize(using = ValidationErrorDeserializer.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValidationError extends RuntimeException {
        private final String field;
        private final Set<String> errors;
        public ValidationError(String field, Set<String> errors) {
            this.field = field;
            this.errors = errors;
        }
        @JsonProperty
        public String getField() { return field; }
        @JsonProperty
        public Set<String> getErrors() { return errors; }
    }

    private T value;
    private boolean valid = true;
    private String fieldName = "";
    private final Set<String> errors = new LinkedHashSet<String>();

    public ValidatorBuilder(T value) {
        setValue(value);
    }
    public ValidatorBuilder(T value, String fieldName) {
        this(value);
        this.fieldName = fieldName;
    }

    protected void invalidate(String reason) {
        errors.add(reason);
        this.valid = false;
        if(onInvalidateAction.equals(OnInvalidateAction.THROW_EXCEPTION)) {
            throw new ValidationError(getField(), getErrors());
        }
    }

    public String getField() { return fieldName; }
    public Set<String> getErrors() { return errors; }
    public boolean isValid() { return valid; }
    protected T getValue() { return value; }
    protected void setValue(T newValue) {
        value = newValue;
    }
    public T build() {
        if(!isValid() && onInvalidateAction.equals(OnInvalidateAction.THROW_EXCEPTION_ON_BUILD)) {
            throw new ValidationError(getField(), getErrors());
        }
        return value;
    }

}
