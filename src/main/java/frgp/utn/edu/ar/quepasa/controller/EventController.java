package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.EventService;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final AuthenticationService authenticationService;
    private final VoteService voteService;

    @Autowired
    public EventController(EventService eventService, AuthenticationService authenticationService, VoteService voteService) {
        this.eventService = eventService;
        this.authenticationService = authenticationService;
        this.voteService = voteService;
    }

    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active, @RequestParam(defaultValue="title,asc") String sort) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(eventService.getEvents(q, pageable, active));
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventPostRequest event) throws Fail {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(eventService.create(event, me));
    }

    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<?> confirmEventAssistance(@PathVariable UUID eventId) {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(eventService.confirmEventAssistance(eventId, me));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getEventsByUser(@PathVariable String username, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.findByUsername(username, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getEventsByAuthUser(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        User me = authenticationService.getCurrentUserOrDie();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.findByOp(me, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable UUID id, @RequestBody EventPatchEditRequest event) throws Fail {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(eventService.update(id, event, me));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id) throws Fail {
        eventService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    /*
    Seccion Agregar Barrio
     */

    @PostMapping("/{eventId}/neighbourhood/{neighbourhoodId}")
    public ResponseEntity<Event> addNeighbourhoodEvent(@PathVariable UUID eventId, @PathVariable Long neighbourhoodId) {
        Event updatedEvent = eventService.addNeighbourhoodEvent(eventId, neighbourhoodId);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{eventId}/neighbourhood/{neighbourhoodId}")
    public ResponseEntity<Event> removeNeighbourhoodEvent(@PathVariable UUID eventId, @PathVariable Long neighbourhoodId) {
        Event updatedEvent = eventService.removeNeighbourhoodEvent(eventId, neighbourhoodId);
        return ResponseEntity.ok(updatedEvent);
    }

    /*
    Seccion Votos
     */
    @GetMapping("/{eventId}/votes")
    public ResponseEntity<VoteCount> getVotes(@PathVariable UUID eventId) {
        return ResponseEntity.ok(voteService.count(Event.identify(eventId)));
    }

    @PostMapping("/{eventId}/votes/up")
    public ResponseEntity<VoteCount> upVote(@PathVariable UUID eventId) {
        return ResponseEntity.ok(voteService.vote(Event.identify(eventId), 1));
    }

    @PostMapping("/{eventId}/votes/down")
    public ResponseEntity<VoteCount> downVote(@PathVariable UUID eventId) {
        return ResponseEntity.ok(voteService.vote(Event.identify(eventId), -1));
    }


    /// Excepciones
    ///
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(new ResponseError(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<?> handleFail(Fail e) {
        return new ResponseEntity<>(new ResponseError(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ResponseError("Invalid request body." + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationError.class)
    public ResponseEntity<ValidationError> handleValidationError(ValidationError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }
}
