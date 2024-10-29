package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.EventCategory;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Event Controller Test")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private NeighbourhoodRepository neighbourhoodRepository;

    @BeforeAll
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        neighbourhoodRepository = Mockito.mock(NeighbourhoodRepository.class);
    }

    @Test
    @DisplayName("GET /api/events - Listar eventos")
    void testGetEvents() throws Exception {
        setAuthContext();

        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("GET /api/events - Listar Eventos Sort")
    void testGetEventsSort() throws Exception {
        setAuthContext();

        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "title,asc")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))))
                .andExpect(jsonPath("$.content[0].title", is("Taller de Arte")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento")
    void testCreateEvent() throws Exception {
        setAuthContext();

        Integer opId = 1;
        long neighbourhoodId = 1L;

        User owner = new User();
        owner.setId(opId);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighbourhoodId);
        neighbourhood.setActive(true);

        when(neighbourhoodRepository.findActiveNeighbourhoodById(neighbourhoodId)).thenReturn(Optional.of(neighbourhood));
        when(neighbourhoodRepository.findById(neighbourhoodId)).thenReturn(Optional.of(neighbourhood));

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");
        eventRequest.setDescription("Descripción de prueba");
        eventRequest.setAddress("Dirección de prueba");
        eventRequest.setStartDate(LocalDateTime.now().plusDays(1));
        eventRequest.setEndDate(LocalDateTime.now().plusDays(2));
        eventRequest.setAudience(Audience.PUBLIC);
        eventRequest.setCategory(EventCategory.EDUCATIVE);

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin titulo")
    void testCreateEventNoTitle() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setDescription("Descripción de prueba");

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("El valor no debe ser nulo. ")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin descripción")
    void testCreateEventNoDescription() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("El valor no debe ser nulo. ")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin dirección")
    void testCreateEventNoAddress() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");
        eventRequest.setDescription("Descripción de prueba");

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("El valor no debe ser nulo. ")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin fecha de inicio")
    void testCreateEventNoStartDate() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");
        eventRequest.setDescription("Descripción de prueba");
        eventRequest.setAddress("Dirección de prueba");

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("La fecha del evento no puede ser nula. ")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin fecha de fin")
    void testCreateEventNoEndDate() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");
        eventRequest.setDescription("Descripción de prueba");
        eventRequest.setAddress("Dirección de prueba");
        eventRequest.setStartDate(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("La fecha del evento no puede ser nula. ")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin audiencia")
    void testCreateEventNoAudience() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");
        eventRequest.setDescription("Descripción de prueba");
        eventRequest.setAddress("Dirección de prueba");
        eventRequest.setStartDate(LocalDateTime.now().plusDays(1));
        eventRequest.setEndDate(LocalDateTime.now().plusDays(2));
        eventRequest.setCategory(EventCategory.EDUCATIVE);

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("Este campo no puede estar nulo. ")));

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events - Crear evento sin categoría")
    void testCreateEventNoCategory() throws Exception {
        setAuthContext();

        EventPostRequest eventRequest = new EventPostRequest();
        eventRequest.setTitle("Evento de prueba");
        eventRequest.setDescription("Descripción de prueba");
        eventRequest.setAddress("Dirección de prueba");
        eventRequest.setStartDate(LocalDateTime.now().plusDays(1));
        eventRequest.setEndDate(LocalDateTime.now().plusDays(2));
        eventRequest.setAudience(Audience.PUBLIC);

        mockMvc.perform(post("/api/events")
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ValidationError.class, result.getResolvedException()))
                .andExpect(jsonPath("$.errors[0]", is("La categoría del evento no puede ser nula. ")));

        clearAuthContext();
    }


    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events/{eventId}/rsvp - Confirmar asistencia a evento")
    void testConfirmEventAssistance() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("cea268b5-1591-4dbe-b72e-77e7ecdad0fc");

        mockMvc.perform(post("/api/events/{eventId}/rsvp", eventId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.event.title", is("Taller de Cerámica")))
            .andExpect(jsonPath("$.event.id", is(eventId.toString())))
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events/{eventId}/rsvp - Confirmar asistencia a evento inexistente")
    void testConfirmEventAssistanceNoEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();

        mockMvc.perform(post("/api/events/{eventId}/rsvp", eventId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Event not found.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andExpect(status().isNotFound())
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/events/{eventId}/rsvp - Confirmar asistencia a evento inactivo")
    void testConfirmEventAssistanceInactiveEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("84b669c6-770d-4dfe-90bc-b67d7702fffb");

        mockMvc.perform(post("/api/events/{eventId}/rsvp", eventId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Event is not active.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andExpect(status().isBadRequest())
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("GET /api/events/{id} - Obtener evento por ID")
    void testGetEventById() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("cea268b5-1591-4dbe-b72e-77e7ecdad0fc");

        mockMvc.perform(get("/api/events/{id}", eventId)
                .with(user("root").password("123456789").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk());

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("GET /api/events/{id} - Obtener evento por ID Inexistente")
    void testGetEventByIdNoAuth() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/{id}", eventId)
                        .with(user("root").password("123456789").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Event not found.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("PATCH /api/events/{id} - Actualizar evento")
    void testUpdateEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("cea268b5-1591-4dbe-b72e-77e7ecdad0fc");

        EventPatchEditRequest updatedEventRequest = new EventPatchEditRequest();
        updatedEventRequest.setTitle("Título actualizado");
        updatedEventRequest.setDescription("Descripción actualizada");

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
        UUID eventId = UUID.fromString("cea268b5-1591-4dbe-b72e-77e7ecdad0fc");
    
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
        UUID eventId = UUID.fromString("cea268b5-1591-4dbe-b72e-77e7ecdad0fc");
        long neighbourhoodId = 1L;

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setId(neighbourhoodId);

        mockMvc.perform(post("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Taller de Cerámica")))
            .andExpect(jsonPath("$.id", is(eventId.toString())))
            .andExpect(jsonPath("$.neighbourhoods", hasSize(2)))
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("POST /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Agregar barrio a evento inexistente")
    void testAddNeighbourhoodEventNoEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();
        long neighbourhoodId = 1L;

        mockMvc.perform(post("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Event not found.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andExpect(status().isNotFound())
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("POST /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Agregar barrio a evento inactivo")
    void testAddNeighbourhoodEventInactiveEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("84b669c6-770d-4dfe-90bc-b67d7702fffb");
        long neighbourhoodId = 1L;

        mockMvc.perform(post("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Event is not active.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andExpect(status().isBadRequest())
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("DELETE /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Eliminar barrio de evento")
    void testRemoveNeighbourhoodEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("cea268b5-1591-4dbe-b72e-77e7ecdad0fc");
        Long neighbourhoodId = 3L;

        mockMvc.perform(delete("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Taller de Cerámica")))
            .andExpect(jsonPath("$.id", is(eventId.toString())))
            .andExpect(jsonPath("$.neighbourhoods", hasSize(0)))
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("DELETE /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Eliminar barrio de evento inexistente")
    void testRemoveNeighbourhoodEventNoEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.randomUUID();
        Long neighbourhoodId = 3L;

        mockMvc.perform(delete("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Event not found.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andExpect(status().isNotFound())
        ;

        clearAuthContext();
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    @DisplayName("DELETE /api/events/{eventId}/neighbourhood/{neighbourhoodId} - Eliminar barrio de evento inactivo")
    void testRemoveNeighbourhoodEventInactiveEvent() throws Exception {
        setAuthContext();
        UUID eventId = UUID.fromString("84b669c6-770d-4dfe-90bc-b67d7702fffb");
        Long neighbourhoodId = 3L;

        mockMvc.perform(delete("/api/events/{eventId}/neighbourhood/{neighbourhoodId}", eventId, neighbourhoodId)
            .with(user("root").password("123456789").roles("ADMIN"))
            .contentType("application/json"))
            .andExpect(result -> assertInstanceOf(Fail.class, result.getResolvedException()))
            .andExpect(result -> assertEquals("Event is not active.", Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andExpect(status().isBadRequest())
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
