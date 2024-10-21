package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRsvpRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("EventControllerTest")
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final EventRepository eventRepository;
    private final EventRsvpRepository eventRsvpRepository;
    private final UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    EventControllerTest(EventRepository eventRepository, EventRsvpRepository eventRsvpRepository, UserRepository userRepository, PostSubtypeRepository postSubtypeRepository, NeighbourhoodRepository neighbourhoodRepository) {
        this.eventRepository = eventRepository;
        this.eventRsvpRepository = eventRsvpRepository;
        this.userRepository = userRepository;
    }

    @BeforeAll void setUp() {

    }

}
