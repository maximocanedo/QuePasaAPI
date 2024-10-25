package frgp.utn.edu.ar.quepasa.service.validators;

public class BooleanBuilder {

    private boolean value;

    public BooleanBuilder() {
        value = true;
    }

    public BooleanBuilder(boolean value) {
        this.value = value;
    }

    public BooleanBuilder and(boolean expression) {
        value &= expression;
        return this;
    }

    public BooleanBuilder or(boolean expression) {
        value |= expression;
        return this;
    }

    public BooleanBuilder not(boolean expression) {
        value ^= expression;
        return this;
    }

    public boolean build() {
        return value;
    }

}
