package frgp.utn.edu.ar.quepasa.service.media;

import frgp.utn.edu.ar.quepasa.model.media.PostPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PostPictureService {
    PostPicture upload(MultipartFile file, String description);

    PostPicture getPictureById(UUID id);

    Page<PostPicture> getPostPics(Integer id, Pageable pageable);

    void delete(UUID id);
}
