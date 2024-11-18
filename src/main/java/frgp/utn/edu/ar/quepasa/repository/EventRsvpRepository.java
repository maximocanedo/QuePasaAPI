package frgp.utn.edu.ar.quepasa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, Integer> {
    Optional<EventRsvp> findByEventAndUser(Event event, User user);
    @Query("select r from event_rsvps r where r.user = :user")
    Optional<EventRsvp> findByUser(@Param("user") int userId);


}
