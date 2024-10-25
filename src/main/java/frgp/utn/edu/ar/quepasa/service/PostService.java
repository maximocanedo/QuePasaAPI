package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;

public interface PostService {
    Page<Post> search(String q, Pageable pageable, boolean active);

    Page<Post> findAll(Pageable pageable, boolean activeOnly);

    Post findById(Integer id);

    Page<Post> findByOp(Integer originalPoster, Pageable pageable);

    Page<Post> findByAudience(Audience audience, Pageable pageable);

    Page<Post> findByType(Integer type, Pageable pageable);

    Page<Post> findBySubtype(Integer subtype, Pageable pageable);

    Post create(PostCreateRequest newPost, User originalPoster);

    Post update(Integer id, PostPatchEditRequest newPost, User originalPoster) throws AccessDeniedException;

    void delete(Integer id, User originalPoster) throws AccessDeniedException;
}
