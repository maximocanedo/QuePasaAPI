package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.ValidatorBuilder;

import java.util.Optional;

public class PostTypeValidator extends ValidatorBuilder<PostTypeValidator, PostType> {
    public PostTypeValidator(Integer id, PostTypeRepository repository) {
        super(repository.findById(id).orElseThrow(), "postType");
    }

    public PostTypeValidator isActive(PostTypeRepository repository) {
        Optional<PostType> optional = repository.findById(getValue().getId());
        if(optional.isPresent()) {
            if(!optional.get().isActive())
                super.invalidate("Registro no disponible. ");
        } else super.invalidate("Registro no encontrado. ");
        return this;
    }

}