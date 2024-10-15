package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRsvpRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.impl.EventServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Servicio de eventos")
public class EventServiceTest {
    private EventRepository eventRepository;
    private EventRsvpRepository eventRsvpRepository;
    private NeighbourhoodRepository neighbourhoodRepository;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        this.eventRepository = Mockito.mock(EventRepository.class);
        this.eventRsvpRepository = Mockito.mock(EventRsvpRepository.class);
        this.neighbourhoodRepository = Mockito.mock(NeighbourhoodRepository.class);
        OwnerService ownerService = Mockito.mock(OwnerService.class);
        this.eventService = new EventServiceImpl(ownerService, eventRepository, neighbourhoodRepository, eventRsvpRepository);
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
    @DisplayName("Obtner Eventos por Op Inexistente")
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
}
