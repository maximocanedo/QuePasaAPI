package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRsvpRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.EventService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service("eventService")
public class EventServiceImpl implements EventService {
    private final OwnerService ownerService;
    private final EventRepository eventRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final EventRsvpRepository eventRsvpRepository;

    @Autowired
    public EventServiceImpl(OwnerService ownerService, EventRepository eventRepository, NeighbourhoodRepository neighbourhoodRepository, EventRsvpRepository eventRsvpRepository) {
        this.ownerService = ownerService;
        this.eventRepository = eventRepository;
        this.neighbourhoodRepository = neighbourhoodRepository;
        this.eventRsvpRepository = eventRsvpRepository;
    }

    @Override
    public Page<Event> getEvents(String query, Pageable pageable, boolean active) {
        return eventRepository.search(query, pageable, active);
    }

    @Override
    public Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
    }

    @Override
    public Page<Event> findByOp(User owner, Pageable pageable) {
        return eventRepository.findByOwner(owner, pageable)
                .orElseThrow(() -> new ResourceNotFoundException("No Events found."));
    }

    @Override
    public Page<Event> findByUsername(String username, Pageable pageable) {
        return eventRepository.findByOwnerUsername(username, pageable)
                .orElseThrow(() -> new ResourceNotFoundException("No Events found."));
    }

    @Override
    public Event create(EventPostRequest event, User owner) {
        /*TODO
        *  -Validacion info*/
        Event newEvent = new Event();
        newEvent.setTitle(event.getTitle());
        newEvent.setDescription(event.getDescription());
        newEvent.setStart(event.getStartDate());
        newEvent.setEnd(event.getEndDate());
        newEvent.setCategory(event.getCategory());
        newEvent.setAudience(event.getAudience());
        newEvent.setAddress(event.getAddress());
        newEvent.setActive(true);
        newEvent.setCreatedAt(Timestamp.from(Instant.now()));
        newEvent.setOwner(owner);
        eventRepository.save(newEvent);
        return newEvent;
    }

    @Override
    public Event update(UUID id, EventPatchEditRequest newEvent, User owner) throws Fail {
        /*TODO
         *  -Validacion info*/
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        ownerService.of(event)
                .isOwner()
                .or()
                .isAdmin()
                .orElseFail();

        if (newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
        if (newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
        if (newEvent.getStartDate() != null) event.setStart(newEvent.getStartDate());
        if (newEvent.getEndDate() != null) event.setEnd(newEvent.getEndDate());
        if (newEvent.getCategory() != null) event.setCategory(newEvent.getCategory());
        if (newEvent.getAudience() != null) event.setAudience(newEvent.getAudience());
        if (newEvent.getAddress() != null) event.setAddress(newEvent.getAddress());

        eventRepository.save(event);
        return event;
    }

    @Override
    public EventRsvp confirmEventAssistance(UUID eventId, User user) {
        EventRsvp newEventRsvp = new EventRsvp();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        newEventRsvp.setEvent(event);
        newEventRsvp.setUser(user);
        eventRsvpRepository.save(newEventRsvp);
        return newEventRsvp;
    }

    @Override
    public void delete(UUID id) throws Fail {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        ownerService.of(event)
                .isOwner()
                .or()
                .isAdmin()
                .or()
                .isModerator()
                .orElseFail();
        event.setActive(false);
        eventRepository.save(event);
    }

    @Override
    public Event addNeighbourhoodsToEvent(UUID eventId, Set<Long> neighbourhoodIds) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Event not found.");
        }

        Event event = eventOptional.get();
        Set<Neighbourhood> neighbourhoods = event.getNeighbourhoods();

        for (Long neighbourhoodId : neighbourhoodIds) {
            neighbourhoodRepository.findById(neighbourhoodId).ifPresent(neighbourhoods::add);
        }

        event.setNeighbourhoods(neighbourhoods);
        return eventRepository.save(event);
    }

    @Override
    public Event removeNeighbourhoodsFromEvent(UUID eventId, Set<Long> neighbourhoodIds) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Event not found.");
        }

        Event event = eventOptional.get();
        Set<Neighbourhood> neighbourhoods = event.getNeighbourhoods();

        for (Long neighbourhoodId : neighbourhoodIds) {
            neighbourhoodRepository.findById(neighbourhoodId).ifPresent(neighbourhoods::remove);
        }

        event.setNeighbourhoods(neighbourhoods);
        return eventRepository.save(event);
    }


}
