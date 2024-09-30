package frgp.utn.edu.ar.quepasa.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.http.MediaType;

@Converter(autoApply = true)
public class MediaTypeConverter implements AttributeConverter<MediaType, String> {

    @Override
    public String convertToDatabaseColumn(MediaType mediaType) {
        return mediaType != null ? mediaType.toString() : null;
    }

    @Override
    public MediaType convertToEntityAttribute(String dbData) {
        return dbData != null ? MediaType.parseMediaType(dbData) : null;
    }
}
