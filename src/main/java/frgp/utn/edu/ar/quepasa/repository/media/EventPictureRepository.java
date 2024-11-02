package frgp.utn.edu.ar.quepasa.repository.media;

import frgp.utn.edu.ar.quepasa.model.media.EventPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventPictureRepository extends JpaRepository<EventPicture, UUID> {
    Optional<EventPicture> findByEventId(UUID eventId);
}
