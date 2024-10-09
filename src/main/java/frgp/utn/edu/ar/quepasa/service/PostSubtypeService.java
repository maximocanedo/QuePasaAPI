package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;

public interface PostSubtypeService {
    Page<PostSubtype> listPostSubtypes(Pageable pageable);

    PostSubtype findById(Integer id);

    Page<PostSubtype> findByType(Integer type, Pageable pageable);

    PostSubtype create(PostSubtypeRequest newSubtype);

    PostSubtype update(Integer id, PostSubtypeRequest newSubtype, User author) throws AccessDeniedException;

    void delete(Integer id, User author) throws AccessDeniedException;
}
