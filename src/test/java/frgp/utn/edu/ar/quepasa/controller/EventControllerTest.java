package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.service.EventService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("EventControllerTest")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @BeforeAll
    void setUp() {}

    @Test
    @DisplayName("GET /api/events - Listar eventos")
    void testGetEvents() throws Exception {
        setAuthContext();

        Event event = new Event();
        event.setTitle("Evento de prueba");
        event.setDescription("Descripción de prueba");

        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventService.getEvents(anyString(), any(), anyBoolean()))
            .thenReturn(eventPage);

        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))))
                .andExpect(jsonPath("$.content[0].title", is("Evento de prueba")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("GET /api/events - Listar eventos Sort")
    void testGetEventsSort() throws Exception {
        setAuthContext();

        Event event = new Event();
        event.setTitle("Evento de prueba");
        event.setDescription("Descripción de prueba");

        Event event2 = new Event();
        event2.setTitle("Evento de prueba 2");
        event2.setDescription("Descripción de prueba 2");

        Page<Event> eventPage = new PageImpl<>(List.of(event, event2));

        when(eventService.getEvents(anyString(), any(), anyBoolean()))
            .thenReturn(eventPage);

        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "title,asc")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))))
                .andExpect(jsonPath("$.content[0].title", is("Evento de prueba")))
                .andExpect(jsonPath("$.content[1].title", is("Evento de prueba 2")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento")
    void testCreateEvent() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de Prueba");
        eventRequest.setDescription("Descripción de prueba");

        Event event = new Event();
        event.setTitle("Evento de Prueba");
        event.setDescription("Descripción de prueba");

        when(eventService.create(any(EventPostRequest.class), any())).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events/{eventId}/rsvp - Confirmar asistencia a evento")
    void testConfirmEventAssistance() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Evento de prueba");

        EventRsvp eventRsvp = new EventRsvp();
        eventRsvp.setEvent(event);

        when(eventService.confirmEventAssistance(any(UUID.class), any())).thenReturn(eventRsvp);

        mockMvc.perform(post("/api/events/{eventId}/rsvp", eventId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.event.title", is("Evento de prueba")))
            .andExpect(jsonPath("$.event.id", is(eventId.toString())))
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("GET /api/events/{id} - Obtener evento por ID")
    void testGetEventById() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Evento de prueba");
        event.setDescription("Descripción de prueba");

        when(eventService.findById(any(UUID.class))).thenReturn(event);

        mockMvc.perform(get("/api/events/{id}", eventId)
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("PATCH /api/events/{id} - Actualizar evento")
    void testUpdateEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();

        EventPatchEditRequest updatedEventRequest = new EventPatchEditRequest();
        updatedEventRequest.setTitle("Título actualizado");
        updatedEventRequest.setDescription("Descripción actualizada");

        Event updatedEvent = new Event();
        updatedEvent.setTitle("Título actualizado");
        updatedEvent.setDescription("Descripción actualizada");

        when(eventService.update(any(UUID.class), any(EventPatchEditRequest.class), any())).thenReturn(updatedEvent);

        mockMvc.perform(patch("/api/events/{id}", eventId)
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updatedEventRequest)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("DELETE /api/events/{id} - Eliminar evento")
    void testDeleteEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();
    
        Mockito.doNothing().when(eventService).delete(eventId);
    
        mockMvc.perform(delete("/api/events/{id}", eventId)
        .with(user("root").password("123456789").roles("ADMIN"))
        .contentType("application/json"))
        .andExpect(status().isOk());
 

    
        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("POST /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Agregar barrio a evento")
    void testAddNeighbourhoodEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();
        long neighbourhoodId = 1L;

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighbourhoodId);

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Evento de prueba");
        event.setNeighbourhoods(Set.of(neighbourhood));

        when(eventService.addNeighbourhoodEvent(any(UUID.class), any(Long.class))).thenReturn(event);

        mockMvc.perform(post("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Evento de prueba")))
            .andExpect(jsonPath("$.id", is(eventId.toString())))
            .andExpect(jsonPath("$.neighbourhoods", hasSize(1)))
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("DELETE /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Eliminar barrio de evento")
    void testRemoveNeighbourhoodEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 1L;

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Evento de prueba");
        event.setNeighbourhoods(Set.of());

        when(eventService.removeNeighbourhoodEvent(any(UUID.class), any(Long.class))).thenReturn(event);

        mockMvc.perform(delete("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Evento de prueba")))
            .andExpect(jsonPath("$.id", is(eventId.toString())))
            .andExpect(jsonPath("$.neighbourhoods", hasSize(0)))
        ;

        clearAuthContext();
    }
    
    private void setAuthContext() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("root")
                .password("123456789")
                .roles("ADMIN")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void clearAuthContext() {
        SecurityContextHolder.clearContext();
    }
}
