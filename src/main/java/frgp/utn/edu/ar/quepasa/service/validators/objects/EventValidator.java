package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.ValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.events.*;

import java.util.function.Consumer;

public class EventValidator extends ValidatorBuilder<EventValidator, Event> {

    public EventValidator(Event value) {
        super(value, "event");
    }

    public EventValidator canAccess(User user) {
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

    public EventValidator title(Consumer<EventTitleValidator> handler) {
        handler.accept(new EventTitleValidator(getValue().getTitle()));
        return this;
    }

    public EventValidator description(Consumer<EventDescriptionValidator> handler) {
        handler.accept(new EventDescriptionValidator(getValue().getDescription()));
        return this;
    }

    public EventValidator address(Consumer<EventAddressValidator> handler) {
        handler.accept(new EventAddressValidator(getValue().getAddress()));
        return this;
    }

    public EventValidator start(Consumer<EventDateValidator> handler) {
        handler.accept(new EventDateValidator(getValue().getStart()));
        return this;
    }

    public EventValidator end(Consumer<EventDateValidator> handler) {
        handler.accept(new EventDateValidator(getValue().getEnd()));
        return this;
    }

    public EventValidator category(Consumer<EventCategoryValidator> handler) {
        handler.accept(new EventCategoryValidator(getValue().getCategory()));
        return this;
    }

    public EventValidator audience(Consumer<AudienceValidator> handler) {
        handler.accept(new AudienceValidator(getValue().getAudience()));
        return this;
    }

}
