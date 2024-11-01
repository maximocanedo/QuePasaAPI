package quepasa.api.validators.comments;

import quepasa.api.validators.commons.builders.StringValidatorBuilder;

public class CommentContentValidator extends StringValidatorBuilder<CommentContentValidator> {

    public static int MAXIMUM_CHAR_LIMIT = 256;
    public static int MINIMUM_CHAR_LIMIT = 0;

    public CommentContentValidator(String value) {
        super(value, "content");
    }

    public CommentContentValidator meetsLimits() {
        return super.meetsLimits(
                MINIMUM_CHAR_LIMIT,
                MAXIMUM_CHAR_LIMIT,
                "El comentario no puede estar vacío",
                "El comentario no puede tener más de 256 caracteres. ");
    }

}
