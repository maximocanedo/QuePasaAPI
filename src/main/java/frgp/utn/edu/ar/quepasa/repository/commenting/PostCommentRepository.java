package frgp.utn.edu.ar.quepasa.repository.commenting;

import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {

    @Query("SELECT c FROM PostComment c WHERE c.post.id = :id AND c.active")
    Page<PostComment> list(Integer id, Pageable pageable);

    @Query("SELECT COUNT(c) FROM PostComment c WHERE c.post.id = :id AND c.active")
    int count(Integer id);

}

