package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, Integer> {
    Optional<EventRsvp> findByEventAndUser(Event event, User user);

}
