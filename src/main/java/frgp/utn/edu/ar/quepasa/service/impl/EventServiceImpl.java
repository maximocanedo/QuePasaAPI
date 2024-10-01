package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.event.EventPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service("eventService")
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;

    @Override
    public Page<Event> listEvent(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Override
    public Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public Event create(Event event, User owner) {
        /*TODO
        *  -Validacion info*/
        event.setActive(true);
        event.setCreatedAt(Timestamp.from(Instant.now()));
        event.setOwner(owner);
        return eventRepository.save(event);
    }

    @Override
    public Event update(UUID id, EventPatchEditRequest newEvent) {
        Event event = findById(id);
        if (newEvent.getTitle() != null) event.setTitle(newEvent.getTitle());
        if (newEvent.getDescription() != null) event.setDescription(newEvent.getDescription());
        if (newEvent.getStartDate() != null) event.setStart(newEvent.getStartDate());
        if (newEvent.getEndDate() != null) event.setEnd(newEvent.getEndDate());
        if (newEvent.getCategory() != null) event.setCategory(newEvent.getCategory());
        if (newEvent.getAudience() != null) event.setAudience(newEvent.getAudience());
        eventRepository.save(event);
        return event;
    }

    @Override
    public void delete(UUID id) {
        eventRepository.deleteById(id);
    }
}
