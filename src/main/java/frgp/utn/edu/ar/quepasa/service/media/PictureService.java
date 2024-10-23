package frgp.utn.edu.ar.quepasa.service.media;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.model.media.Document;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface PictureService {

    Picture upload(MultipartFile file, String description);

    RawPicture getRawPictureById(UUID id);

    RawPicture getRawPictureById(String id);

    Optional<Picture> getPictureById(UUID id);

    Optional<Picture> getPictureById(String id);

    Page<Picture> getMyPics(Pageable pageable);

    void delete(UUID id);
}
