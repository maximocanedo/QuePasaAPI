package frgp.utn.edu.ar.quepasa.service.validators.comments;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class CommentContentValidatorBuilder extends ValidatorBuilder<String> {

    public static int MAXIMUM_CHAR_LIMIT = 256;
    public static int MINIMUM_CHAR_LIMIT = 0;

    public CommentContentValidatorBuilder(String value) {
        super(value, "content");
    }

    public CommentContentValidatorBuilder trim() {
        setValue(getValue().trim());
        return this;
    }

    public CommentContentValidatorBuilder meetsLimits() {
        if(getValue().length() > MAXIMUM_CHAR_LIMIT)
            super.invalidate("El comentario no puede tener más de 256 caracteres. ");
        else if(getValue().isBlank() || getValue().length() < MINIMUM_CHAR_LIMIT)
            super.invalidate("El comentario no puede estar vacío");
        return this;
    }

}
