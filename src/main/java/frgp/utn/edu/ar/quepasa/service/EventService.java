package frgp.utn.edu.ar.quepasa.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;


public interface EventService {
    Page<Event> getEvents(String query, Pageable pageable, boolean active);

    Event findById(UUID id) throws Fail;

    Page<Event> findByOp(User owner, Boolean active, Pageable pageable) throws Fail;

    Page<Event> findByUsername(String username, Pageable pageable) throws Fail;

    Page<Event> findByAudience(Audience audience, Pageable pageable, boolean active) throws Fail;

    Page<Event> findByEventCategory(EventCategory eventCategory, Pageable pageable, boolean active) throws Fail;

    Event create(EventPostRequest event, User owner) throws Fail;

    EventRsvp confirmEventAssistance(UUID eventId, User user) throws Fail;

    Event update(UUID id, EventPatchEditRequest event, User owner) throws Fail;

    void delete(UUID id) throws Fail;

    Event addNeighbourhoodEvent(UUID eventId, Long neighbourhoodId) throws Fail;

    Event removeNeighbourhoodEvent(UUID eventId, Long neighbourhoodId) throws Fail;

    List<EventRsvp> findRsvpsByUser(User user, boolean confirmed);

}
