package frgp.utn.edu.ar.quepasa.repository.media;

import frgp.utn.edu.ar.quepasa.model.media.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

}
