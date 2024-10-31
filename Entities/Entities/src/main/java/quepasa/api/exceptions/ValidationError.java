package quepasa.api.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import quepasa.api.converters.ValidationErrorDeserializer;
import quepasa.api.converters.ValidationErrorSerializer;

import java.util.Set;

@JsonSerialize(using = ValidationErrorSerializer.class)
@JsonDeserialize(using = ValidationErrorDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationError extends RuntimeException {
    private final String field;
    private final Set<String> errors;

    public ValidationError(String field, Set<String> errors) {
        this.field = field;
        this.errors = errors;
    }

    @JsonProperty
    public String getField() {
        return field;
    }

    @JsonProperty
    public Set<String> getErrors() {
        return errors;
    }
}
