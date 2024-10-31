package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import quepasa.api.validators.commons.builders.ValidatorBuilder;

public class PictureValidator extends ValidatorBuilder<PictureValidator, Picture> {
    public PictureValidator(Picture value) {
        super(value, "picture");
    }
    public PictureValidator isActive(PictureRepository repository) {
        var optional = repository.findById(getValue().getId());
        if(optional.isEmpty())
            super.invalidate("Imagen no encontrada. ");
        else if(!optional.get().isActive())
            super.invalidate("Imagen no disponible. ");
        return this;
    }
    public PictureValidator isOwner(PictureRepository repository, User user, UserRepository users) {
        var optional = repository.findById(getValue().getId());
        var x = users.findByUsername(user.getUsername());
        if(x.isEmpty() || !x.get().isActive()) {
            super.invalidate("Usuario no encontrado.");
            return this;
        }
        if(optional.isEmpty() || !optional.get().isActive()) {
            super.invalidate("Imagen no encontrada. ");
            return this;
        }
        var picture = optional.get();
        var owner = picture.getOwner();
        var claimer = x.get();
        if(!owner.getId().equals(claimer.getId())) {
            super.invalidate("Imagen no disponible.");
        }
        return this;
    }
}
