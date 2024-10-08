package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRsvpRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

public class EventServiceTest {
    @Mock private EventServiceImpl eventService;
    @Mock private EventRepository eventRepository;
    @Mock private EventRsvpRepository eventRsvpRepository;
    @Mock private NeighbourhoodRepository neighbourhoodRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Obtener Todos los Eventos Activos")
    void findAllEvents_EventsFound_ReturnAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);

        Event event = new Event();
        Page<Event> mockPage = new PageImpl<>(List.of(event));

        when(eventRepository.search("", pageable, true)).thenReturn(mockPage);

        Page<Event> events = eventService.getEvents("", pageable, true);

        //assertNotNull(events);
        assertFalse(events.isEmpty());
    }
}
