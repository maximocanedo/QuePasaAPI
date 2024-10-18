package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface EventService {
    Page<Event> getEvents(String query, Pageable pageable, boolean active);

    Event findById(UUID id);

    Page<Event> findByOp(User owner, Pageable pageable);

    Page<Event> findByUsername(String username, Pageable pageable);

    Event create(EventPostRequest event, User owner) throws Fail;

    EventRsvp confirmEventAssistance(UUID eventId, User user);

    Event update(UUID id, EventPatchEditRequest newEvent, User owner) throws Fail;

    void delete(UUID id) throws Fail;

    Event addNeighbourhoodEvent(UUID eventId, Long neighbourhoodId);

    Event removeNeighbourhoodEvent(UUID eventId, Long neighbourhoodId);
    

}
