package frgp.utn.edu.ar.quepasa.repository.media;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.media.PostPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostPictureRepository extends JpaRepository<PostPicture, UUID> {
    Page<PostPicture> findByPost(Post post, Pageable pageable);
}