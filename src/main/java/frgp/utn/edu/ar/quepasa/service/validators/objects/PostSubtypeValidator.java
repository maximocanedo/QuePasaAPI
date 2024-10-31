package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import quepasa.api.validators.commons.builders.ValidatorBuilder;

import java.util.Optional;

public class PostSubtypeValidator extends ValidatorBuilder<PostSubtypeValidator, PostSubtype> {
    public PostSubtypeValidator(Integer id, PostSubtypeRepository repository) {
        super(repository.findById(id).orElseThrow(), "postSubtype");
    }

    public PostSubtypeValidator isActive(PostSubtypeRepository repository) {
        Optional<PostSubtype> optional = repository.findById(getValue().getId());
        if(optional.isPresent()) {
            if(!optional.get().isActive())
                super.invalidate("Registro no disponible. ");
        } else super.invalidate("Registro no encontrado. ");
        return this;
    }

}
