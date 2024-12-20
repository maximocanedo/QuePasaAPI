package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSubtypeService {
    Page<PostSubtype> search(String q, Pageable pageable, boolean active);

    Page<PostSubtype> findAll(Pageable pageable, boolean activeOnly);

    PostSubtype findById(Integer id);

    Page<PostSubtype> findByType(Integer type, Pageable pageable);

    PostSubtype create(PostSubtypeRequest newSubtype);

    PostSubtype update(Integer id, PostSubtypeRequest newSubtype);

    void delete(Integer id);
}
