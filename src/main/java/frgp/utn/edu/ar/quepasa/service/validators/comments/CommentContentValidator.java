package frgp.utn.edu.ar.quepasa.service.validators.comments;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class CommentContentValidator extends ValidatorBuilder<CommentContentValidator, String> {

    public static int MAXIMUM_CHAR_LIMIT = 256;
    public static int MINIMUM_CHAR_LIMIT = 0;

    public CommentContentValidator(String value) {
        super(value, "content");
    }

    public CommentContentValidator trim() {
        setValue(getValue().trim());
        return this;
    }

    public CommentContentValidator meetsLimits() {
        if(getValue().length() > MAXIMUM_CHAR_LIMIT)
            super.invalidate("El comentario no puede tener más de 256 caracteres. ");
        else if(getValue().isBlank() || getValue().length() < MINIMUM_CHAR_LIMIT)
            super.invalidate("El comentario no puede estar vacío");
        return this;
    }

}
