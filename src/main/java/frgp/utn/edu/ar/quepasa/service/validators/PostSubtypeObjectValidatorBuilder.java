package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;

import java.util.Optional;

public class PostSubtypeObjectValidatorBuilder extends ValidatorBuilder<PostSubtype> {
    public PostSubtypeObjectValidatorBuilder(Integer id, PostSubtypeRepository repository) {
        super(repository.findById(id).orElseThrow(), "postSubtype");
    }

    public PostSubtypeObjectValidatorBuilder isActive(PostSubtypeRepository repository) {
        Optional<PostSubtype> optional = repository.findById(getValue().getId());
        if(optional.isPresent()) {
            if(!optional.get().isActive())
                super.invalidate("Registro no disponible. ");
        } else super.invalidate("Registro no encontrado. ");
        return this;
    }

}
