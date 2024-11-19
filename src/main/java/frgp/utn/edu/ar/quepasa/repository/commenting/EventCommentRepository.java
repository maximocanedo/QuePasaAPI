package frgp.utn.edu.ar.quepasa.repository.commenting;

import frgp.utn.edu.ar.quepasa.model.commenting.EventComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, UUID> {

    @Query("SELECT e FROM EventComment e WHERE e.event.id = :id AND e.active = true")
    Page<EventComment> list(UUID id, Pageable pageable);

    @Query("SELECT COUNT(c) FROM EventComment c WHERE c.event.id = :id AND c.active = true")
    int count(UUID id);

}
