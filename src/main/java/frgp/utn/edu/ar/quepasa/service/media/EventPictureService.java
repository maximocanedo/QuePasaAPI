package frgp.utn.edu.ar.quepasa.service.media;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.model.media.EventPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface EventPictureService {
    EventPicture upload(MultipartFile file, UUID eventId, String description);

    EventPicture getPictureById(UUID id);

    RawPicture getRawPictureById(UUID id);

    Page<EventPicture> getEventPics(UUID eventId, Pageable pageable);

    void delete(UUID id);
}
