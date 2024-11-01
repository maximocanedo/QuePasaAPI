package frgp.utn.edu.ar.quepasa.config.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import quepasa.api.exceptions.ValidationError;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ValidationErrorDeserializer extends JsonDeserializer<ValidationError> {
    @Override
    public ValidationError deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String field = node.get("field").asText();
        Set<String> errors = new HashSet<>();
        node.get("errors").forEach(errorNode -> errors.add(errorNode.asText()));
        return new ValidationError(field, errors);
    }
}