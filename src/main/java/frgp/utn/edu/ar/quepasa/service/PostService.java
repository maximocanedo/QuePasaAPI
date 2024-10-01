package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;

public interface PostService {
    Page<Post> listPost(Pageable pageable);

    Post findById(Integer id);

    Post create(PostCreateRequest newPost, User originalPoster);

    Post update(Integer id, PostPatchEditRequest newPost, User originalPoster) throws AccessDeniedException;

    void delete(Integer id, User originalPoster) throws AccessDeniedException;
}
