package frgp.utn.edu.ar.quepasa.config.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;

import java.io.IOException;

public class FailSerializer extends JsonSerializer<Fail> {
    @Override
    public void serialize(Fail value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("message", value.getMessage());
        gen.writeEndObject();
    }
}