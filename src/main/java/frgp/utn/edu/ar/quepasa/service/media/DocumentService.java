package frgp.utn.edu.ar.quepasa.service.media;

import frgp.utn.edu.ar.quepasa.data.response.RawDocument;
import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.model.media.Document;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface DocumentService {

    Document upload(MultipartFile file, String description);

    RawDocument getRawDocumentById(UUID id);

    RawDocument getRawDocumentById(String id);

    Optional<Document> getDocumentById(UUID id);

    Optional<Document> getDocumentById(String id);

    Page<Document> getMyDocuments(Pageable pageable);

    void delete(UUID id);

}
