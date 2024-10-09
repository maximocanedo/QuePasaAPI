package frgp.utn.edu.ar.quepasa.config.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.MediaType;

import java.io.IOException;

public class MediaTypeSerializer extends JsonSerializer<MediaType> {
    @Override
    public void serialize(MediaType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String mediaTypeString = value.getType() + "/" + value.getSubtype();
        gen.writeString(mediaTypeString);
    }
}

