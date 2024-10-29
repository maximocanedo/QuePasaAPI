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
import frgp.utn.edu.ar.quepasa.service.validators.objects.AudienceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    /**
     * This method is used to get all the events
     * @param query search query to filter events
     * @param pageable pagination information
     * @param active status of the event
     **/
    @Override
    public Page<Event> getEvents(String query, Pageable pageable, boolean active) {
        return eventRepository.search(query, pageable, active);
    }

    /**
     * This method is used to get an event by its id
     * @param id id of the event
     * @return event
     * @throws Fail if the event is not found
     */
    @Override
    public Event findById(UUID id) throws Fail {
        return eventRepository.findById(id)
                .orElseThrow(() -> new Fail("Event not found.", HttpStatus.NOT_FOUND));
    }

    /**
     * This method is used to get all the events by the Original Poster
     * @param owner owner of the event
     * @param pageable pagination information
     * @return events
     * @throws Fail if no events are found
     */
    @Override
    public Page<Event> findByOp(User owner, Pageable pageable) throws Fail {
        return eventRepository.findByOwnerAndActive(owner, true, pageable)
                .orElseThrow(() -> new Fail("No Events found.", HttpStatus.NOT_FOUND))
                .map(commentService::populate)
                .map(voteService::populate);
    }

    /**
     * This method is used to get all the events by the username of a user
     * @param username username of the event owner
     * @param pageable pagination information
     * @return events
     * @throws Fail if no events are found
     */
    @Override
    public Page<Event> findByUsername(String username, Pageable pageable) throws Fail {
        return eventRepository.findByOwnerUsername(username, pageable)
                .orElseThrow(() -> new Fail("No Events found.", HttpStatus.NOT_FOUND))
                .map(commentService::populate)
                .map(voteService::populate);
    }

    /**
     * This method is used to create an event
     * @param event event to be created
     * @param owner actual logged user
     * @return event
     * @throws Fail if the event creation fails
     */
    @Override
    public Event create(EventPostRequest event, User owner) throws Fail {
        Event newEvent = new Event();

        var title = new EventTitleValidator(event.getTitle()).meetsLimits().build();
        newEvent.setTitle(title);

        var description = new EventDescriptionValidator(event.getDescription()).meetsLimits().build();
        newEvent.setDescription(description);

        var address = new EventAddressValidator(event.getAddress()).meetsLimits().build();
        newEvent.setAddress(address);

        var startDate = new EventDateValidator(event.getStartDate()).isNotNull().isNotPast().build();
        newEvent.setStart(startDate);

        var endDate = new EventDateValidator(event.getEndDate()).isNotNull().isNotPast().isAfterStartDate(startDate).build();
        newEvent.setEnd(endDate);

        var category = new EventCategoryValidator(event.getCategory()).isNotNull().build();
        newEvent.setCategory(category);

        var audience = new AudienceValidator(event.getAudience()).isNotNull().build();
        newEvent.setAudience(audience);

        newEvent.setActive(true);
        newEvent.setCreatedAt(Timestamp.from(Instant.now()));
        newEvent.setOwner(owner);
        eventRepository.save(newEvent);
        commentService.populate(voteService.populate(newEvent));
        return newEvent;
    }

    /**
     * This method is used to update an event
     * @param id id of the event
     * @param newEvent event information to be updated
     * @param owner actual logged user
     * @return event
     * @throws Fail if the event update fails
     */
    @Override
    public Event update(UUID id, EventPatchEditRequest newEvent, User owner) throws Fail {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new Fail("Event not found.", HttpStatus.NOT_FOUND));
        ownerService.of(event)
                .isAdmin()
                .isOwner()
                .orElseFail();

        if (newEvent.getTitle() != null) event.setTitle(new EventTitleValidator(newEvent.getTitle()).meetsLimits().build());

        if (newEvent.getDescription() != null) event.setDescription(new EventDescriptionValidator(newEvent.getDescription()).isNotNull().meetsLimits().build());

        if (newEvent.getAddress() != null) event.setAddress(new EventAddressValidator(newEvent.getAddress()).isNotNull().meetsLimits().build());

        if (newEvent.getStartDate() != null) event.setStart(new EventDateValidator(newEvent.getStartDate()).isNotPast().build());

        if (newEvent.getEndDate() != null) event.setEnd(new EventDateValidator(newEvent.getEndDate()).isNotPast().isAfterStartDate(event.getStart()).build());

        if (newEvent.getCategory() != null) event.setCategory(new EventCategoryValidator(newEvent.getCategory()).build());

        if (newEvent.getAudience() != null) event.setAudience(new AudienceValidator(newEvent.getAudience()).build());

        eventRepository.save(event);
        commentService.populate(voteService.populate(event));
        return event;
    }

    /**
     * This method is used to confirm the assistance to an event
     * @param eventId id of the event
     * @param user actual logged user
     * @return eventRsvp
     * @throws Fail if the event assistance confirmation fails
     */
    @Override
    public EventRsvp confirmEventAssistance(UUID eventId, User user) throws Fail {
        EventRsvp newEventRsvp = new EventRsvp();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Fail("Event not found.", HttpStatus.NOT_FOUND));
        if (!event.isActive()) throw new Fail("Event is not active.", HttpStatus.BAD_REQUEST);
        newEventRsvp.setEvent(event);
        newEventRsvp.setUser(user);
        eventRsvpRepository.save(newEventRsvp);
        return newEventRsvp;
    }

    /**
     * This method is used to delete an event
     * @param id id of the event
     * @throws Fail if the event deletion fails
     */
    @Override
    public void delete(UUID id) throws Fail {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new Fail("Event not found.", HttpStatus.NOT_FOUND));
        ownerService.of(event)
                .isOwner()
                .isAdmin()
                .orElseFail();
        event.setActive(false);
        eventRepository.save(event);
    }

    /**
     * This method is used to add a neighbourhood to an event
     * @param eventId id of the event
     * @param neighbourhoodId id of the neighbourhood
     * @return event
     * @throws Fail if the event or the neighbourhood is not found
     * @throws Fail if the event is not active
     */
    @Override
    public Event addNeighbourhoodEvent(UUID eventId, Long neighbourhoodId) throws Fail {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Fail("Event not found.", HttpStatus.NOT_FOUND));

        ownerService.of(event)
                .isOwner()
                .isAdmin()
                .orElseFail();

        if (!event.isActive()) throw new Fail("Event is not active.", HttpStatus.BAD_REQUEST);


        Set<Neighbourhood> neighbourhoods = event.getNeighbourhoods();

        Neighbourhood neighbourhood =  neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new Fail("Neighbourhood not found.", HttpStatus.NOT_FOUND));
        neighbourhoods.add(neighbourhood);

        event.setNeighbourhoods(neighbourhoods);
        eventRepository.save(event);
        commentService.populate(voteService.populate(event));
        return event;
    }

    /**
     * This method is used to remove a neighbourhood from an event
     * @param eventId id of the event
     * @param neighbourhoodId id of the neighbourhood
     * @return event
     * @throws Fail if the event or the neighbourhood is not found
     * @throws Fail if the event is not active
     */
    @Override
    public Event removeNeighbourhoodEvent(UUID eventId, Long neighbourhoodId) throws Fail {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Fail("Event not found.", HttpStatus.NOT_FOUND));

        ownerService.of(event)
                .isOwner()
                .isAdmin()
                .orElseFail();

        if (!event.isActive()) throw new Fail("Event is not active.", HttpStatus.BAD_REQUEST);

        Set<Neighbourhood> neighbourhoods = event.getNeighbourhoods();

        Neighbourhood neighbourhood =  neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new Fail("Neighbourhood not found.", HttpStatus.NOT_FOUND));
        neighbourhoods.remove(neighbourhood);

        event.setNeighbourhoods(neighbourhoods);

        eventRepository.save(event);
        commentService.populate(voteService.populate(event));
        return event;
    }
}