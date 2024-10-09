package frgp.utn.edu.ar.quepasa.config.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.http.MediaType;

import java.io.IOException;

public class MediaTypeDeserializer extends JsonDeserializer<MediaType> {

    @Override
    public MediaType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String mediaTypeString = p.getText();
        String[] parts = mediaTypeString.split("/");
        if (parts.length != 2) {
            throw new IOException("Invalid media type format. Expected 'type/subtype', but was given '" + mediaTypeString + "'. ");
        }
        return new MediaType(parts[0], parts[1]);
    }
}
