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
import frgp.utn.edu.ar.quepasa.service.CommentService;
import frgp.utn.edu.ar.quepasa.service.EventService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import frgp.utn.edu.ar.quepasa.service.validators.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service("eventService")
public class EventServiceImpl implements EventService {
  
    private final OwnerService ownerService;
    private final VoteService voteService;
    private final EventRepository eventRepository;
    private final CommentService commentService;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final EventRsvpRepository eventRsvpRepository;

    @Autowired
    public EventServiceImpl(
            OwnerService ownerService,
            VoteService voteService,
            EventRepository eventRepository, CommentService commentService,
            NeighbourhoodRepository neighbourhoodRepository,
            EventRsvpRepository eventRsvpRepository
    ) {
        this.ownerService = ownerService;
        this.voteService = voteService;
        this.eventRepository = eventRepository;
        this.commentService = commentService;
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
                .orElseThrow(() -> new ResourceNotFoundException("No Events found."))
                .map(commentService::populate)
                .map(voteService::populate);
    }

    @Override
    public Page<Event> findByUsername(String username, Pageable pageable) {
        return eventRepository.findByOwnerUsername(username, pageable)
                .orElseThrow(() -> new ResourceNotFoundException("No Events found."))
                .map(commentService::populate)
                .map(voteService::populate);
    }

    @Override
    public Event create(EventPostRequest event, User owner) throws Fail {
        Event newEvent = new Event();

        var title = new EventTitleValidatorBuilder(event.getTitle()).isNotNull().isNotEmpty().isNotTooLong().build();
        newEvent.setTitle(title);

        var description = new EventDescriptionValidatorBuilder(event.getDescription()).isNotNull().isNotEmpty().isNotTooLong().build();
        newEvent.setDescription(description);

        var address = new EventAddressValidatorBuilder(event.getAddress()).isNotNull().isNotEmpty().isNotTooLong().build();
        newEvent.setAddress(address);

        var startDate = new EventDateValidatorBuilder(event.getStartDate()).isNotNull().isNotPast().build();
        newEvent.setStart(startDate);

        var endDate = new EventDateValidatorBuilder(event.getEndDate()).isNotNull().isNotPast().isNotBefore(startDate).build();
        newEvent.setEnd(endDate);

        var category = new EventCategoryValidatorBuilder(event.getCategory()).isNotNull().isNotInvalid().build();
        newEvent.setCategory(category);

        var audience = new EventAudienceValidatorBuilder(event.getAudience()).isNotNull().isNotInvalid().build();
        newEvent.setAudience(audience);

        newEvent.setActive(true);
        newEvent.setCreatedAt(Timestamp.from(Instant.now()));
        newEvent.setOwner(owner);
        eventRepository.save(newEvent);
        voteService.populate(newEvent);
        return newEvent;
    }

    @Override
    public Event update(UUID id, EventPatchEditRequest newEvent, User owner) throws Fail {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        ownerService.of(event)
                .isAdmin()
                .isOwner()
                .orElseFail();

        if (newEvent.getTitle() != null) event.setTitle(new EventTitleValidatorBuilder(newEvent.getTitle()).isNotEmpty().isNotTooLong().build());

        if (newEvent.getDescription() != null) event.setDescription(new EventDescriptionValidatorBuilder(newEvent.getDescription()).isNotEmpty().isNotTooLong().build());

        if (newEvent.getAddress() != null) event.setAddress(new EventAddressValidatorBuilder(newEvent.getAddress()).isNotEmpty().isNotTooLong().build());

        if (newEvent.getStartDate() != null) event.setStart(new EventDateValidatorBuilder(newEvent.getStartDate()).isNotPast().build());

        if (newEvent.getEndDate() != null) event.setEnd(new EventDateValidatorBuilder(newEvent.getEndDate()).isNotPast().isNotBefore(event.getStart()).build());

        if (newEvent.getCategory() != null) event.setCategory(new EventCategoryValidatorBuilder(newEvent.getCategory()).isNotInvalid().build());

        if (newEvent.getAudience() != null) event.setAudience(new EventAudienceValidatorBuilder(newEvent.getAudience()).isNotInvalid().build());

        eventRepository.save(event);
        return commentService.populate(voteService.populate(event));
    }

    @Override
    public EventRsvp confirmEventAssistance(UUID eventId, User user) throws Fail {
        EventRsvp newEventRsvp = new EventRsvp();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        if (!event.isActive()) throw new Fail("Event is not active.");
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
                .isAdmin()
                .orElseFail();
        event.setActive(false);
        eventRepository.save(event);
    }

    @Override
    public Event addNeighbourhoodEvent(UUID eventId, Long neighbourhoodId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));

        ownerService.of(event)
                .isOwner()
                .isAdmin()
                .orElseFail();

        if (!event.isActive()) throw new Fail("Event is not active.");


        Set<Neighbourhood> neighbourhoods = event.getNeighbourhoods();

        Neighbourhood neighbourhood =  neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new ResourceNotFoundException("Neighbourhood not found."));
        neighbourhoods.add(neighbourhood);

        event.setNeighbourhoods(neighbourhoods);
        eventRepository.save(event);
        return commentService.populate(voteService.populate(event));
    }

    @Override
    public Event removeNeighbourhoodEvent(UUID eventId, Long neighbourhoodId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));

        ownerService.of(event)
                .isOwner()
                .isAdmin()
                .orElseFail();

        if (!event.isActive()) throw new Fail("Event not found.");

        Set<Neighbourhood> neighbourhoods = event.getNeighbourhoods();

        Neighbourhood neighbourhood =  neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new ResourceNotFoundException("Neighbourhood not found."));
        neighbourhoods.remove(neighbourhood);

        event.setNeighbourhoods(neighbourhoods);

        eventRepository.save(event);

        return commentService.populate(voteService.populate(event));
    }
}
