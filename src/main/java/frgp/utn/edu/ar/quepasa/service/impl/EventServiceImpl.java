package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRsvpRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.EventService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.validators.events.EventDateValidatorBuilder;
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
        if (event.getTitle() == null) {
            throw new Fail("Title is required.");
        }
        newEvent.setTitle(event.getTitle());
        if (event.getDescription() == null) {
            throw new Fail("Description is required.");
        }
        newEvent.setDescription(event.getDescription());
        if (event.getStartDate() == null) {
            throw new Fail("Start date is required.");
        }
        var startDate = new EventDateValidatorBuilder(event.getStartDate()).isNotPast().build();
        newEvent.setStart(startDate);
        if (event.getEndDate() == null) {
            throw new Fail("End date is required.");
        }
        var endDate = new EventDateValidatorBuilder(event.getEndDate()).isNotPast().isNotBefore(startDate).build();
        newEvent.setEnd(endDate);
        if (event.getCategory() == null) {
            throw new Fail("Category is required.");
        }
        try {
            EventCategory.valueOf(String.valueOf(event.getCategory()));
        } catch (IllegalArgumentException e) {
            throw new Fail("Invalid category.");
        }
        newEvent.setCategory(event.getCategory());
        if (event.getAudience() == null) {
            throw new Fail("Audience is required.");
        }
        try {
            Audience.valueOf(String.valueOf(event.getAudience()));
        } catch (IllegalArgumentException e) {
            throw new Fail("Invalid audience.");
        }
        newEvent.setAudience(event.getAudience());
        if (event.getAddress() == null) {
            throw new Fail("Address is required.");
        }
        newEvent.setAddress(event.getAddress());
        newEvent.setActive(true);
        newEvent.setCreatedAt(Timestamp.from(Instant.now()));
        newEvent.setOwner(owner);
        eventRepository.save(newEvent);
        return newEvent;
    }

    @Override
    public Event update(UUID id, EventPatchEditRequest newEvent, User owner) throws Fail {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        ownerService.of(event)
                .isOwner()
                .or()
                .isAdmin()
                .orElseFail();

        if (newEvent.getTitle() == null) {
            throw new Fail("Title is required.");
        }
        event.setTitle(newEvent.getTitle());
        if (newEvent.getDescription() == null) {
            throw new Fail("Description is required.");
        }
        event.setDescription(newEvent.getDescription());
        if (newEvent.getStartDate() == null) {
            throw new Fail("Start date is required.");
        }
        var startDate = new EventDateValidatorBuilder(newEvent.getStartDate()).isNotPast().build();
        event.setStart(startDate);
        if (newEvent.getEndDate() == null) {
            throw new Fail("End date is required.");
        }
        var endDate = new EventDateValidatorBuilder(newEvent.getEndDate()).isNotPast().isNotBefore(startDate).build();
        event.setEnd(endDate);
        if (newEvent.getCategory() == null) {
            throw new Fail("Category is required.");
        }
        try {
            EventCategory.valueOf(String.valueOf(newEvent.getCategory()));
        } catch (IllegalArgumentException e) {
            throw new Fail("Invalid category.");
        }
        event.setCategory(newEvent.getCategory());
        if (newEvent.getAudience() == null) {
            throw new Fail("Audience is required.");
        }
        try {
            Audience.valueOf(String.valueOf(newEvent.getAudience()));
        } catch (IllegalArgumentException e) {
            throw new Fail("Invalid audience.");
        }
        event.setAudience(newEvent.getAudience());
        if (newEvent.getAddress() == null) {
            throw new Fail("Address is required.");
        }
        event.setAddress(newEvent.getAddress());
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
