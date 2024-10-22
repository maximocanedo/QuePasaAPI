package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.request.event.EventPostRequest;
import frgp.utn.edu.ar.quepasa.model.Event;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

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
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    @DisplayName("GET /api/events - Listar eventos")
    void testGetEvents() throws Exception {
        // Prepara el mock del servicio para devolver un evento simulado
        when(eventService.getEvents(anyString(), any(), anyBoolean()))
            .thenReturn(Page.empty());

        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))));
    }

@Test
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@DisplayName("POST /api/events - Crear evento")
void testCreateEvent() throws Exception {

    EventPostRequest eventRequest = new EventPostRequest();
    eventRequest.setTitle("Evento de Prueba");
    eventRequest.setDescription("Descripción de prueba");
    Event event = new Event();
    event.setTitle("Evento de Prueba");
    event.setDescription("Descripción de prueba");
    when(eventService.create(any(EventPostRequest.class), any())).thenReturn(event);

    mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(eventRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Evento de Prueba")))
            .andExpect(jsonPath("$.description", is("Descripción de prueba")));
}


    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    @DisplayName("GET /api/events/{id} - Obtener evento por ID")
    void testGetEventById() throws Exception {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setTitle("Evento de prueba");
        event.setDescription("Descripción de prueba");

        when(eventService.findById(any())).thenReturn(event);

        mockMvc.perform(get("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Evento de prueba")))
                .andExpect(jsonPath("$.description", is("Descripción de prueba")));
    }

@Test
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@DisplayName("PATCH /api/events/{id} - Actualizar evento")
void testUpdateEvent() throws Exception {
    UUID eventId = UUID.randomUUID();
    
    EventPatchEditRequest updatedEventRequest = new EventPatchEditRequest();
    updatedEventRequest.setTitle("Título actualizado");
    updatedEventRequest.setDescription("Descripción actualizada");

    Event updatedEvent = new Event();
    updatedEvent.setTitle("Título actualizado");
    updatedEvent.setDescription("Descripción actualizada");

    when(eventService.update(any(UUID.class), any(EventPatchEditRequest.class), any())).thenReturn(updatedEvent);

    mockMvc.perform(patch("/api/events/{id}", eventId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEventRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Título actualizado")))
            .andExpect(jsonPath("$.description", is("Descripción actualizada")));
}

@Test
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@DisplayName("DELETE /api/events/{id} - Eliminar evento")
void testDeleteEvent() throws Exception {
    UUID eventId = UUID.randomUUID();
    
    Mockito.doNothing().when(eventService).delete(eventId);

    mockMvc.perform(delete("/api/events/{id}", eventId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    mockMvc.perform(get("/api/events/{id}", eventId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
}
}
