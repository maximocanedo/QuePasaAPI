package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import quepasa.api.exceptions.ValidationError;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Devuelve una lista paginada de eventos según la consulta.
     *
     * @param q         Texto de búsqueda.
     * @param page      Página a obtener.
     * @param size      Tamaño de la página.
     * @param active    Si se desean obtener solo los eventos activos.
     * @param sort      Campo y orden de ordenamiento. Ej: "title,asc".
     * @return          Página de eventos encontrados.
     */
    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active, @RequestParam(defaultValue="title,asc") String sort) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(eventService.getEvents(q, pageable, active));
    }

    /**
     * Crea un nuevo evento.
     *
     * @param event Detalles del evento a crear.
     * @return Entidad de respuesta con el evento creado.
     * @throws Fail Si ocurre un error durante la creación del evento.
     * @throws ValidationError Si el evento no cumple con las validaciones.
     */
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventPostRequest event) throws Fail {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(eventService.create(event, me));
    }

    /**
     * Confirma la asistencia a un evento.
     *
     * @param eventId ID del evento al que se quiere confirmar asistencia.
     * @return Entidad de respuesta con el RSVP (confirmación de asistencia) del evento.
     * @throws Fail si ocurre un error durante la confirmación de asistencia.
     */
    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<?> confirmEventAssistance(@PathVariable UUID eventId) throws Fail {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(eventService.confirmEventAssistance(eventId, me));
    }

    /**
     * Obtiene un evento por su ID.
     *
     * @param id ID del evento a obtener.
     * @return Entidad de respuesta con los detalles del evento.
     * @throws Fail Si el evento no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable UUID id) throws Fail {
        return ResponseEntity.ok(eventService.findById(id));
    }

    /**
     * Obtiene una lista paginada de eventos creados por un usuario específico.
     *
     * @param username Nombre de usuario del creador de los eventos.
     * @param page     Número de página a obtener.
     * @param size     Tamaño de la página.
     * @return         Entidad de respuesta con la página de eventos del usuario.
     * @throws Fail    Si el evento no se encuentra.
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getEventsByUser(@PathVariable String username, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws Fail {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.findByUsername(username, pageable));
    }

    /**
     * Obtiene una lista paginada de eventos creados por el usuario autenticado.
     *
     * @param page  Número de página a obtener.
     * @param size  Tamaño de la página.
     * @return      Entidad de respuesta con la página de eventos del usuario autenticado.
     * @throws Fail Si no se en encuentran eventos y/o todos los eventos del estan inactivos.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getEventsByAuthUser(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) throws Fail {
        User me = authenticationService.getCurrentUserOrDie();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.findByOp(me, pageable));
    }

    @GetMapping("/audience/{audience}")
    public ResponseEntity<?> getEventsByAudience(@PathVariable Audience audience, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) throws Fail {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.findByAudience(audience, pageable));
    }

    @GetMapping("/eventCategory/{category}")
    public ResponseEntity<?> getEventsByEventCategory(@PathVariable EventCategory category, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) throws Fail {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.findByEventCategory(category, pageable));
    }

    /**
     * Actualiza un evento existente.
     *
     * @param id       ID del evento a actualizar.
     * @param event    Detalles del evento a actualizar.
     * @return         Entidad de respuesta con los detalles del evento actualizado.
     * @throws Fail    Si el evento no se encuentra o no se tiene permisos para actualizarlo.
     * @throws ValidationError Si el evento no cumple con las validaciones.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable UUID id, @RequestBody EventPatchEditRequest event) throws Fail {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(eventService.update(id, event, me));
    }

    /**
     * Elimina un evento existente.
     *
     * @param id       ID del evento a eliminar.
     * @return         Entidad de respuesta con un estado 204 (No Content) si se elimina correctamente.
     * @throws Fail    Si el evento no se encuentra o no se tiene permisos para eliminarlo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id) throws Fail {
        eventService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    /*   Seccion Agregar Barrio  */

    /**
     * Agrega un barrio a un evento existente.
     *
     * @param eventId       ID del evento al que se agregar  el barrio.
     * @param neighbourhoodId   ID del barrio a agregar.
     * @return         Entidad de respuesta con los detalles del evento actualizado.
     * @throws Fail    Si el evento o el barrio no se encuentran o no se tiene permisos para agregar.
     */
    @PostMapping("/{eventId}/neighbourhood/{neighbourhoodId}")
    public ResponseEntity<Event> addNeighbourhoodEvent(@PathVariable UUID eventId, @PathVariable Long neighbourhoodId) {
        Event updatedEvent = eventService.addNeighbourhoodEvent(eventId, neighbourhoodId);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Elimina un barrio de un evento existente.
     *
     * @param eventId       ID del evento del que se eliminar  el barrio.
     * @param neighbourhoodId   ID del barrio a eliminar.
     * @return         Entidad de respuesta con los detalles del evento actualizado.
     * @throws Fail    Si el evento o el barrio no se encuentran o no se tiene permisos para eliminar.
     */
    @DeleteMapping("/{eventId}/neighbourhood/{neighbourhoodId}")
    public ResponseEntity<Event> removeNeighbourhoodEvent(@PathVariable UUID eventId, @PathVariable Long neighbourhoodId) {
        Event updatedEvent = eventService.removeNeighbourhoodEvent(eventId, neighbourhoodId);
        return ResponseEntity.ok(updatedEvent);
    }

    
    /**
     * Devuelve la cantidad total de votos de un evento.
     *
     * @param eventId       ID del evento del que se quieren obtener los votos.
     * @return         Entidad de respuesta con la cantidad total de votos del evento.
     */
    @GetMapping("/{eventId}/votes")
    public ResponseEntity<VoteCount> getVotes(@PathVariable UUID eventId) {
        return ResponseEntity.ok(voteService.count(Event.identify(eventId)));
    }

    /**
     * Vota positivamente un evento existente.
     *
     * @param eventId       ID del evento que se va a votar.
     * @return         Entidad de respuesta con la cantidad actualizada de votos del evento.
     */
    @PostMapping("/{eventId}/votes/up")
    public ResponseEntity<VoteCount> upVote(@PathVariable UUID eventId) {
        return ResponseEntity.ok(voteService.vote(Event.identify(eventId), 1));
    }
    /**
     * Vota negativamente un evento existente.
     *
     * @param eventId       ID del evento que se va a votar.
     * @return         Entidad de respuesta con la cantidad actualizada de votos del evento.
     */
    @PostMapping("/{eventId}/votes/down")
    public ResponseEntity<VoteCount> downVote(@PathVariable UUID eventId) {
        return ResponseEntity.ok(voteService.vote(Event.identify(eventId), -1));
    }
}
