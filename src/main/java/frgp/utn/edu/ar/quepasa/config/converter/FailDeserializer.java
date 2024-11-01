package frgp.utn.edu.ar.quepasa.config.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import quepasa.api.exceptions.ValidationError;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FailDeserializer extends JsonDeserializer<Fail> {
    @Override
    public Fail deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String message = node.get("message").asText();
        return new Fail(message);
    }
}