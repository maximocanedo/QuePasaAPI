package frgp.utn.edu.ar.quepasa.service.validators;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;

public class PostValidatorBuilder extends ValidatorBuilder<Post> {

    public PostValidatorBuilder(Post value) {
        super(value, "post");
    }

    public PostValidatorBuilder canAccess(User user) {
        var authorsNeighbourhood = getValue().getOwner().getNeighbourhood();
        var authorsCity = authorsNeighbourhood.getCity();
        var authorsState = authorsCity.getSubdivision();
        var authorsNation = authorsState.getCountry();
        switch(getValue().getAudience()) {
            case Audience.NEIGHBORHOOD:
                if(!authorsNeighbourhood.equals(user.getNeighbourhood()))
                    super.invalidate("Este elemento no está disponible en tu barrio. ");
                break;
            case Audience.CITY:
                if(!authorsCity.equals(user.getNeighbourhood().getCity()))
                    super.invalidate("Este elemento no está disponible en tu ciudad. ");
                break;
            case Audience.SUBNATIONAL:
                if(!authorsState.equals(user.getNeighbourhood().getCity().getSubdivision()))
                    super.invalidate("Este elemento no está disponible en tu región. ");
                break;
            case Audience.NATIONAL:
                if(!authorsNation.equals(user.getNeighbourhood().getCity().getSubdivision().getCountry()))
                    super.invalidate("Este elemento no está disponible en tu país. ");
                break;
        }
        return this;
    }

}
