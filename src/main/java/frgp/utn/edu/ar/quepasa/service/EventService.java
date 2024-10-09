package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.UUID;
import java.util.Set;


public interface EventService {
    Page<Event> getEvents(String query, Pageable pageable, boolean active);

    Event findById(UUID id);

    Page<Event> findByOp(User owner, Pageable pageable);

    Page<Event> findByUsername(String username, Pageable pageable);

    Event create(EventPostRequest event, User owner);

    EventRsvp confirmEventAssistance(UUID eventId, User user);

    Event update(UUID id, EventPatchEditRequest newEvent, User owner) throws AccessDeniedException;

    void delete(UUID id);

    Event addNeighbourhoodsToEvent(UUID eventId, Set<Long> neighbourhoodIds);

    Event removeNeighbourhoodsFromEvent(UUID eventId, Set<Long> neighbourhoodIds);
    

}
