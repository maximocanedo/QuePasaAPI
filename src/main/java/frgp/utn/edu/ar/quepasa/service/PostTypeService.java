package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostTypeService {
    Page<PostType> search(String q, Pageable pageable, boolean active);

    Page<PostType> findAll(Pageable pageable, boolean activeOnly);

    PostType findById(Integer id);

    PostType findBySubtype(Integer id);

    PostType create(String description);

    PostType update(Integer id, String description);

    void delete(Integer id);
}
