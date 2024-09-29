package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> listPost(Pageable pageable);

    Post findById(Integer id);

    void update(Integer id);

    void delete(Integer id);
}
