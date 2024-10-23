package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRsvpRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.EventServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Servicio de eventos")
public class EventServiceTest {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventRsvpRepository eventRsvpRepository;
    private NeighbourhoodRepository neighbourhoodRepository;
    private AuthenticationServiceImpl authenticationService;
    private EventServiceImpl eventService;
    private OwnerServiceImpl ownerService;
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        this.eventRepository = mock(EventRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.eventRsvpRepository = mock(EventRsvpRepository.class);
        this.neighbourhoodRepository = mock(NeighbourhoodRepository.class);
        this.authenticationService = mock(AuthenticationServiceImpl.class);
        ownerService = new OwnerServiceImpl(authenticationService);
        this.voteService = mock(VoteService.class);
        this.eventService = new EventServiceImpl(ownerService, voteService, eventRepository, neighbourhoodRepository, eventRsvpRepository);
    }


    @Test
    @DisplayName("Obtener Todos los Eventos Activos")
    void findAllActiveEvents_EventsFound_ReturnAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        Event event1 = new Event();
        event1.setActive(true);
        Event event2 = new Event();
        event2.setActive(true);

        Page<Event> mockPage = new PageImpl<>(Arrays.asList(event1, event2));
        when(eventRepository.search("", pageable, true)).thenReturn(mockPage);

        Page<Event> events = eventService.getEvents("", pageable, true);

        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(2, events.getTotalElements());
        assertTrue(events.getContent().get(0).isActive());
        assertTrue(events.getContent().get(1).isActive());
    }

    @Test
    @DisplayName("Obtener Todos los Eventos Inactivos")
    void findAllInactiveEvents_EventsFound_ReturnAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        Event event1 = new Event();
        event1.setActive(false);
        Event event2 = new Event();
        event2.setActive(false);

        Page<Event> mockPage = new PageImpl<>(Arrays.asList(event1, event2));
        when(eventRepository.search("", pageable, true)).thenReturn(mockPage);

        Page<Event> events = eventService.getEvents("", pageable, true);

        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(2, events.getTotalElements());
        assertFalse(events.getContent().get(0).isActive());
        assertFalse(events.getContent().get(1).isActive());
    }

    @Test
    @DisplayName("Busqueda Obtener Todos los Eventos")
    void findAllEvents_EventsFound_ReturnAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        Event event1 = new Event();
        event1.setTitle("event1");
        event1.setActive(true);
        Event event2 = new Event();
        event2.setTitle("event2");
        event2.setActive(true);

        Page<Event> mockPage = new PageImpl<>(Arrays.asList(event1, event2));
        when(eventRepository.search("event", pageable, true)).thenReturn(mockPage);

        Page<Event> events = eventService.getEvents("event", pageable, true);

        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(2, events.getTotalElements());
        assertTrue(events.getContent().get(0).getTitle().contains("event1"));
        assertTrue(events.getContent().get(1).getTitle().contains("event2"));
    }


    @Test
    @DisplayName("Obtener Evento por ID")
    void findEventById_EventFound_ReturnEvent() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event foundEvent = eventService.findById(eventId);

        assertNotNull(foundEvent);
        assertEquals(eventId, foundEvent.getId());
    }

    @Test
    @DisplayName("Obtener Evento por ID Inexistente")
    void findEventById_EventNotFound_ReturnNull() {
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findById(eventId)
        );

        assertEquals("Event not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Obtener Eventos por Op")
    void findEventsByOp_EventsFound_ReturnAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        User owner = new User();
        owner.setUsername("owner");

        Event event1 = new Event();
        event1.setOwner(owner);
        Event event2 = new Event();
        event2.setOwner(owner);

        Page<Event> mockPage = new PageImpl<>(Arrays.asList(event1, event2));
        when(eventRepository.findByOwner(owner, pageable)).thenReturn(Optional.of(mockPage));

        Page<Event> events = eventService.findByOp(owner, pageable);

        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(2, events.getTotalElements());
        assertEquals(owner.getUsername(), events.getContent().get(0).getOwner().getUsername());
        assertEquals(owner.getUsername(), events.getContent().get(1).getOwner().getUsername());
    }

    @Test
    @DisplayName("Obtener Eventos por Op Inexistente")
    void findEventsByOp_NoEventsFound_ReturnNull() {
        Pageable pageable = PageRequest.of(0, 10);

        User owner = new User();
        owner.setUsername("owner");

        when(eventRepository.findByOwner(owner, pageable)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findByOp(owner, pageable)
        );

        assertEquals("No Events found.", exception.getMessage());
    }

    @Test
    @DisplayName("Obtener Eventos por Username")
    void findEventsByUsername_EventsFound_ReturnAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        User owner = new User();
        owner.setUsername("owner");

        Event event1 = new Event();
        event1.setOwner(owner);
        Event event2 = new Event();
        event2.setOwner(owner);

        Page<Event> mockPage = new PageImpl<>(Arrays.asList(event1, event2));
        when(eventRepository.findByOwnerUsername(owner.getUsername(), pageable)).thenReturn(Optional.of(mockPage));

        Page<Event> events = eventService.findByUsername(owner.getUsername(), pageable);

        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(2, events.getTotalElements());
        assertEquals(owner.getUsername(), events.getContent().get(0).getOwner().getUsername());
        assertEquals(owner.getUsername(), events.getContent().get(1).getOwner().getUsername());
    }

    @Test
    @DisplayName("Obtener Eventos por Username Inexistente")
    void findEventsByUsername_NoEventsFound_ReturnNull() {
        Pageable pageable = PageRequest.of(0, 10);

        User owner = new User();
        owner.setUsername("owner");

        when(eventRepository.findByOwnerUsername(owner.getUsername(), pageable)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findByUsername(owner.getUsername(), pageable)
        );

        assertEquals("No Events found.", exception.getMessage());
    }

    @Test
    @DisplayName("Crear Evento")
    void createEvent_ValidEvent_ReturnEvent() throws Fail {
        User owner = new User();
        owner.setUsername("owner");

        Event event = new Event();
        event.setTitle("event");
        event.setDescription("description");
        event.setAddress("address");
        event.setStart(LocalDateTime.now().plusHours(1));
        event.setEnd(LocalDateTime.now().plusHours(2));
        event.setCategory(EventCategory.CINEMA);
        event.setAudience(Audience.PUBLIC);
        event.setOwner(owner);

        when(eventRepository.save(event)).thenReturn(event);

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setDescription("description");
        eventPostRequest.setAddress("address");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setCategory(EventCategory.CINEMA);
        eventPostRequest.setAudience(Audience.PUBLIC);

        Event createdEvent = eventService.create(eventPostRequest, owner);

        assertNotNull(createdEvent);
        assertEquals("event", createdEvent.getTitle());
        assertEquals("description", createdEvent.getDescription());
        assertEquals(owner.getUsername(), createdEvent.getOwner().getUsername());
    }

    @Test
    @DisplayName("Crear Evento sin Titulo")
    void createEvent_NoTitle_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setDescription("description");
        eventPostRequest.setAddress("address");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setCategory(EventCategory.CINEMA);
        eventPostRequest.setAudience(Audience.PUBLIC);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Title of the event cannot be null."));
    }

    @Test
    @DisplayName("Crear Evento sin Descripcion")
    void createEvent_NoDescription_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setAddress("address");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setCategory(EventCategory.CINEMA);
        eventPostRequest.setAudience(Audience.PUBLIC);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Description of the event cannot be null."));
    }

    @Test
    @DisplayName("Crear Evento sin Direccion")
    void createEvent_NoAddress_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setDescription("description");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setCategory(EventCategory.CINEMA);
        eventPostRequest.setAudience(Audience.PUBLIC);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Address of the event cannot be null."));
    }

    @Test
    @DisplayName("Crear Evento sin Fecha de Inicio")
    void createEvent_NoStartDate_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setDescription("description");
        eventPostRequest.setAddress("address");
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setCategory(EventCategory.CINEMA);
        eventPostRequest.setAudience(Audience.PUBLIC);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Date of the event cannot be null."));
    }

    @Test
    @DisplayName("Crear Evento sin Fecha de Fin")
    void createEvent_NoEndDate_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setDescription("description");
        eventPostRequest.setAddress("address");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setCategory(EventCategory.CINEMA);
        eventPostRequest.setAudience(Audience.PUBLIC);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Date of the event cannot be null."));
    }

    @Test
    @DisplayName("Crear Evento sin Categoria")
    void createEvent_NoCategory_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setDescription("description");
        eventPostRequest.setAddress("address");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setAudience(Audience.PUBLIC);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Category of the event cannot be null."));
    }

    @Test
    @DisplayName("Crear Evento sin Audiencia")
    void createEvent_NoAudience_ThrowFail() {
        User owner = new User();
        owner.setUsername("owner");

        EventPostRequest eventPostRequest = new EventPostRequest();
        eventPostRequest.setTitle("event");
        eventPostRequest.setDescription("description");
        eventPostRequest.setAddress("address");
        eventPostRequest.setStartDate(LocalDateTime.now().plusHours(1));
        eventPostRequest.setEndDate(LocalDateTime.now().plusHours(2));
        eventPostRequest.setCategory(EventCategory.CINEMA);

        ValidatorBuilder.ValidationError exception = assertThrows(
                ValidatorBuilder.ValidationError.class,
                () -> eventService.create(eventPostRequest, owner)
        );

        assertTrue(exception.getErrors().contains("Audience of the event cannot be null."));
    }

    @Test
    @DisplayName("Actualizar Evento")
    void updateEvent_ValidEvent_ReturnEvent() throws Fail {
        UUID eventId = UUID.randomUUID();
        String username = "owner";

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("event");
        event.setDescription("description");
        event.setAddress("address");
        event.setCategory(EventCategory.CINEMA);
        event.setAudience(Audience.PUBLIC);
        event.setOwner(owner);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventPatchEditRequest eventPatchEditRequest = new EventPatchEditRequest();
        eventPatchEditRequest.setTitle("event");
        eventPatchEditRequest.setDescription("description");
        eventPatchEditRequest.setAddress("address");
        eventPatchEditRequest.setCategory(EventCategory.CINEMA);
        eventPatchEditRequest.setAudience(Audience.PUBLIC);


        assertDoesNotThrow(() -> {
                    Event updatedEvent = eventService.update(eventId, eventPatchEditRequest, owner);
                    assertNotNull(updatedEvent);
                    assertEquals(eventId, updatedEvent.getId());
                    assertEquals(event.getTitle(), updatedEvent.getTitle());
                    assertEquals(event.getDescription(), updatedEvent.getDescription());
                    assertEquals(event.getAddress(), updatedEvent.getAddress());
                    assertEquals(event.getCategory(), updatedEvent.getCategory());
                    assertEquals(event.getAudience(), updatedEvent.getAudience());
                }
        );
        clearAuthContext();
    }

    @Test
    @DisplayName("Actualizar Evento Inexistente")
    void updateEvent_EventNotFound_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        String username = "owner";

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.ADMIN);

        setAuthContext(username, "ADMIN");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventPatchEditRequest eventPatchEditRequest = new EventPatchEditRequest();
        eventPatchEditRequest.setTitle("event");
        eventPatchEditRequest.setDescription("description");
        eventPatchEditRequest.setAddress("address");
        eventPatchEditRequest.setCategory(EventCategory.CINEMA);
        eventPatchEditRequest.setAudience(Audience.PUBLIC);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.update(eventId, eventPatchEditRequest, owner)
        );

        assertEquals("Event not found.", exception.getMessage());
        clearAuthContext();
    }

    @Test
    @DisplayName("Actualizar Evento Permisos Insuficientes")
    void updateEvent_InsufficientPermissions_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        String username = "owner";
        String username2 = "user";

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.USER);

        User user = new User();
        user.setUsername(username2);
        user.setRole(Role.USER);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);

        setAuthContext(username, "USER");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventPatchEditRequest eventPatchEditRequest = new EventPatchEditRequest();
        eventPatchEditRequest.setTitle("event");
        eventPatchEditRequest.setDescription("description");
        eventPatchEditRequest.setAddress("address");
        eventPatchEditRequest.setCategory(EventCategory.CINEMA);
        eventPatchEditRequest.setAudience(Audience.PUBLIC);

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.update(eventId, eventPatchEditRequest, user)
        );

        assertTrue(exception.getMessage().contains("Error de accesos."));
        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar Evento")
    void deleteEvent_ValidEvent_ReturnEvent() {
        UUID eventId = UUID.randomUUID();
        String username = "owner";

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.ADMIN);
        setAuthContext(username, "ADMIN");

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);
        event.setActive(true);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertDoesNotThrow(() ->
                eventService.delete(eventId)
        );

        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar Evento Inexistente")
    void deleteEvent_EventNotFound_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        String username = "owner";

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.ADMIN);
        setAuthContext(username, "ADMIN");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.delete(eventId)
        );

        assertEquals("Event not found.", exception.getMessage());
        clearAuthContext();
    }

    @Test
    @DisplayName("Eliminar Evento Permisos Insuficientes")
    void deleteEvent_InsufficientPermissions_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        String username = "owner";
        String username2 = "user";

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.USER);

        User user = new User();
        user.setUsername(username2);
        user.setRole(Role.USER);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);

        setAuthContext(username, "USER");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.delete(eventId)
        );

        assertTrue(exception.getMessage().contains("Error de accesos."));
        clearAuthContext();
    }

    @Test
    @DisplayName("Confirmar Asistencia a Evento")
    void confirmEventAssistance_ValidEvent_ReturnEventRsvp() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setUsername("user");

        Event event = new Event();
        event.setId(eventId);
        event.setActive(true);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventRsvp eventRsvp = new EventRsvp();
        eventRsvp.setEvent(event);
        eventRsvp.setUser(user);

        when(eventRsvpRepository.save(eventRsvp)).thenReturn(eventRsvp);

        EventRsvp confirmedEventRsvp = eventService.confirmEventAssistance(eventId, user);

        assertNotNull(confirmedEventRsvp);
        assertEquals(eventId, confirmedEventRsvp.getEvent().getId());
        assertEquals(user.getUsername(), confirmedEventRsvp.getUser().getUsername());
    }


    @Test
    @DisplayName("Confirmar Asistencia a Evento Inexistente")
    void confirmEventAssistance_EventNotFound_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setUsername("user");

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.confirmEventAssistance(eventId, user)
        );

        assertEquals("Event not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Confirmar Asistencia a Evento Inactivo")
    void confirmEventAssistance_EventInactive_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setUsername("user");

        Event event = new Event();
        event.setId(eventId);
        event.setActive(false);

        setAuthContext(user.getUsername(), "USER");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.confirmEventAssistance(eventId, user)
        );

        assertTrue(exception.getMessage().contains("Event is not active."));
    }

    @Test
    @DisplayName("Agregar Barrio a Evento")
    void addNeighbourhoodEvent_ValidEvent_ReturnEvent() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighbourhoodId);

        Set<Neighbourhood> neighbourhoods = new HashSet<>();
        neighbourhoods.add(neighbourhood);

        Event event = new Event();
        event.setId(eventId);
        event.setNeighbourhoods(neighbourhoods);
        event.setActive(true);

        User owner = new User();
        owner.setUsername("owner");
        owner.setRole(Role.ADMIN);
        setAuthContext(owner.getUsername(), "ADMIN");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(neighbourhood));

        Event updatedEvent = eventService.addNeighbourhoodEvent(eventId, neighbourhoodId);

        assertNotNull(updatedEvent);
        assertEquals(eventId, updatedEvent.getId());
        assertTrue(updatedEvent.getNeighbourhoods().contains(neighbourhood));
    }

    @Test
    @DisplayName("Agregar Barrio a Evento Inexistente")
    void addNeighbourhoodEvent_EventNotFound_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        User owner = new User();
        owner.setUsername("owner");
        setAuthContext(owner.getUsername(), "ADMIN");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(new Neighbourhood()));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.addNeighbourhoodEvent(eventId, neighbourhoodId)
        );

        assertEquals("Event not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Agregar Barrio a Evento Permisos Insuficientes")
    void addNeighbourhoodEvent_InsufficientPermissions_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        User owner = new User();
        owner.setUsername("owner");
        owner.setRole(Role.USER);

        User user = new User();
        user.setUsername("user");
        user.setRole(Role.USER);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);

        setAuthContext(owner.getUsername(), "USER");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(new Neighbourhood()));

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.addNeighbourhoodEvent(eventId, neighbourhoodId)
        );

        assertTrue(exception.getMessage().contains("Error de accesos."));
    }

    @Test
    @DisplayName("Agregar Barrio a Evento Inactivo")
    void addNeighbourhoodEvent_EventInactive_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        User owner = new User();
        owner.setUsername("owner");
        owner.setRole(Role.ADMIN);
        setAuthContext(owner.getUsername(), "ADMIN");

        Event event = new Event();
        event.setId(eventId);
        event.setActive(false);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(new Neighbourhood()));

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.addNeighbourhoodEvent(eventId, neighbourhoodId)
        );

        assertTrue(exception.getMessage().contains("Event is not active."));
    }

    @Test
    @DisplayName("Eliminar Barrio de Evento")
    @WithMockUser(username = "owner", roles = { "ADMIN" })
    void removeNeighbourhoodEvent_ValidEvent_ReturnEvent() {
        UUID eventId = UUID.randomUUID();
        String username = "owner";
        Long neighbourhoodId = 1L;

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighbourhoodId);

        Set<Neighbourhood> neighbourhoods = new HashSet<>();
        neighbourhoods.add(neighbourhood);

        User owner = new User();
        owner.setUsername(username);
        owner.setRole(Role.ADMIN);
        setAuthContext(username, "ADMIN");

        Event event = new Event();
        event.setId(eventId);
        event.setNeighbourhoods(neighbourhoods);
        event.setOwner(owner);
        event.setActive(true);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(neighbourhood));

        Event updatedEvent = eventService.removeNeighbourhoodEvent(eventId, neighbourhoodId);

        assertNotNull(updatedEvent);
        assertEquals(eventId, updatedEvent.getId());
        assertFalse(updatedEvent.getNeighbourhoods().contains(neighbourhood));
    }

    @Test
    @DisplayName("Eliminar Barrio de Evento Inexistente")
    void removeNeighbourhoodEvent_EventNotFound_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(new Neighbourhood()));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.removeNeighbourhoodEvent(eventId, neighbourhoodId)
        );

        assertEquals("Event not found.", exception.getMessage());
    }

    @Test
    @DisplayName("Eliminar Barrio de Evento Permisos Insuficientes")
    void removeNeighbourhoodEvent_InsufficientPermissions_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        User owner = new User();
        owner.setUsername("owner");
        owner.setRole(Role.USER);

        User user = new User();
        user.setUsername("user");
        user.setRole(Role.USER);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);

        setAuthContext(owner.getUsername(), "USER");

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(new Neighbourhood()));

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.removeNeighbourhoodEvent(eventId, neighbourhoodId)
        );

        assertTrue(exception.getMessage().contains("Error de accesos."));
    }

    @Test
    @DisplayName("Eliminar Barrio de Evento Inactivo")
    void removeNeighbourhoodEvent_EventInactive_ThrowFail() {
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        User owner = new User();
        owner.setUsername("owner");
        owner.setRole(Role.ADMIN);
        setAuthContext(owner.getUsername(), "ADMIN");

        Event event = new Event();
        event.setId(eventId);
        event.setActive(false);

        when(authenticationService.getCurrentUserOrDie()).thenReturn(owner);
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(new Neighbourhood()));

        Fail exception = assertThrows(
                Fail.class,
                () -> eventService.removeNeighbourhoodEvent(eventId, neighbourhoodId)
        );

        assertTrue(exception.getMessage().contains("Event is not active."));
    }

    private void setAuthContext(String username, String role) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("password")
                .roles(role)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthContext() {
        SecurityContextHolder.clearContext();
    }
}
