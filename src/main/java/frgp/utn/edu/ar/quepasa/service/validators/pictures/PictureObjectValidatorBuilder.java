package frgp.utn.edu.ar.quepasa.service.validators.pictures;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class PictureObjectValidatorBuilder extends ValidatorBuilder<Picture> {
    public PictureObjectValidatorBuilder(Picture value) {
        super(value, "picture");
    }
    public PictureObjectValidatorBuilder isActive(PictureRepository repository) {
        var optional = repository.findById(getValue().getId());
        if(optional.isEmpty())
            super.invalidate("Imagen no encontrada. ");
        else if(!optional.get().isActive())
            super.invalidate("Imagen no disponible. ");
        return this;
    }
    public PictureObjectValidatorBuilder isOwner(PictureRepository repository, User user, UserRepository users) {
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
