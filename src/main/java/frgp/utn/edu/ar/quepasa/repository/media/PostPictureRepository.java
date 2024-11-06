package frgp.utn.edu.ar.quepasa.repository.media;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.media.PostPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostPictureRepository extends JpaRepository<PostPicture, UUID> {
    Page<PostPicture> findByPost(Post post, Pageable pageable);
}