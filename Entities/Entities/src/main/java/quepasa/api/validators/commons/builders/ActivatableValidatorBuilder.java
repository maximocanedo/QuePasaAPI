package quepasa.api.validators.commons.builders;

import quepasa.api.entities.Activatable;

@SuppressWarnings("unchecked")
public class ActivatableValidatorBuilder<T extends ActivatableValidatorBuilder<T>> extends ObjectValidatorBuilder<ActivatableValidatorBuilder<T>, Activatable> {

    public ActivatableValidatorBuilder(Activatable value, String fieldName) { super(value, fieldName); }
    public ActivatableValidatorBuilder(Activatable value) { super(value, "entity"); }

    public T isActive(String feedback) {
        super.isNotNull();
        if(!getValue().isActive())
            super.invalidate(feedback);
        return (T) this;
    }
    public T isActive() {
        return this
                .isNotNull()
                .isActive("El elemento en cuestión no está habilitado. ");
    }

    public T isNotActive(String feedback) {
        super.isNotNull();
        if(getValue().isActive())
            super.invalidate(feedback);
        return (T) this;
    }
    public T isNotActive() {
        return this
                .isNotNull()
                .isNotActive("El elemento en cuestión está habilitado. ");
    }



}
