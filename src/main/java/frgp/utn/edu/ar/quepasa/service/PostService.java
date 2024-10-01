package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> listPost(Pageable pageable);

    Post findById(Integer id);

    Post update(Integer id, PostPatchEditRequest newPost);

    void delete(Integer id);
}
