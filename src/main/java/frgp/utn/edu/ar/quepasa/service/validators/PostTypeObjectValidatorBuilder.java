package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;

import java.util.Optional;

public class PostTypeObjectValidatorBuilder extends ValidatorBuilder<PostType> {
    public PostTypeObjectValidatorBuilder(PostType value) { super(value, "postType"); }
    public PostTypeObjectValidatorBuilder(Integer id, PostTypeRepository repository) {
        super(repository.findById(id).orElseThrow(), "postType");
    }

    public PostTypeObjectValidatorBuilder isActive(PostTypeRepository repository) {
        Optional<PostType> optional = repository.findById(getValue().getId());
        if(optional.isPresent()) {
            if(!optional.get().isActive())
                super.invalidate("Registro no disponible. ");
        } else super.invalidate("Registro no encontrado. ");
        return this;
    }

}
