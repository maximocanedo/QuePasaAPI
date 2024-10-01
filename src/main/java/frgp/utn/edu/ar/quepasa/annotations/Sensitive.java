package frgp.utn.edu.ar.quepasa.annotations;

import frgp.utn.edu.ar.quepasa.config.converter.SensitiveDataConverter;
import jakarta.persistence.Convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Convert(converter = SensitiveDataConverter.class)
public @interface Sensitive {

}
