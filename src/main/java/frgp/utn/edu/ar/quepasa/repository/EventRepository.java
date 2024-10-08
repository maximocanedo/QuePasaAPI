package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Page<Event>> findByOwner(User owner, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.owner.username = :username")
    Optional<Page<Event>> findByOwnerUsername(@NotNull String username, @NotNull Pageable pageable);

    @Query("SELECT e FROM Event e WHERE (e.title LIKE %:query% OR e.description LIKE %:query%) AND e.active = :active")
    Page<Event> search(@NotNull String query, @NotNull Pageable pageable, boolean active);
}
