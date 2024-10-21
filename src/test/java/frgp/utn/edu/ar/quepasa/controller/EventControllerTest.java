package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp(){}

    @Test
    @DisplayName("GET /api/events - Listar eventos")
    void testGetEvents() throws Exception {
        mockMvc.perform(get("/api/events")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(10))))
                .andExpect(jsonPath("$.content[0].id").exists());
    }

    @Test
    @DisplayName("POST /api/events - Crear evento")
    void testCreateEvent() throws Exception {
        Event event = new Event();
        event.setTitle("Evento de Prueba");
        event.setDescription("Descripción de prueba");

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk()) // Verifica que la respuesta HTTP sea 200
                .andExpect(jsonPath("$.title", is("Evento de Prueba")))
                .andExpect(jsonPath("$.description", is("Descripción de prueba")));
    }

    @Test
    @DisplayName("GET /api/events/{id} - Obtener evento por ID")
    void testGetEventById() throws Exception {
        Event event = new Event();
        event.setTitle("Evento de prueba");
        event.setDescription("Descripción de prueba");
        event = eventRepository.save(event);

        mockMvc.perform(get("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Evento de prueba")))
                .andExpect(jsonPath("$.description", is("Descripción de prueba")));
    }

    @Test
    @DisplayName("PATCH /api/events/{id} - Actualizar evento")
    void testUpdateEvent() throws Exception {
        Event event = new Event();
        event.setTitle("Título original");
        event.setDescription("Descripción original");
        event = eventRepository.save(event);

        Event updatedEvent = new Event();
        updatedEvent.setTitle("Título actualizado");
        updatedEvent.setDescription("Descripción actualizada");

        mockMvc.perform(patch("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Título actualizado")))
                .andExpect(jsonPath("$.description", is("Descripción actualizada")));
    }

    @Test
    @DisplayName("DELETE /api/events/{id} - Eliminar evento")
    void testDeleteEvent() throws Exception {
        Event event = new Event();
        event.setTitle("Evento a eliminar");
        event.setDescription("Descripción del evento a eliminar");
        event = eventRepository.save(event);

        mockMvc.perform(delete("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
