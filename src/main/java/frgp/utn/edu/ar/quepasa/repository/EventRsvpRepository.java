package frgp.utn.edu.ar.quepasa.repository;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.EventRsvp;
import frgp.utn.edu.ar.quepasa.model.User;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, Integer> {
    Optional<EventRsvp> findByEventAndUser(@NotNull Event event, @NotNull User user);

    List<EventRsvp> findByUserAndConfirmed(User user, boolean confirmed);
}
