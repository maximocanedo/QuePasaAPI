package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;

public interface PostTypeService {
    Page<PostType> listPostTypes(Pageable pageable);

    PostType findById(Integer id);

    PostType create(String description, User author) throws AccessDeniedException;

    PostType update(Integer id, String description, User author) throws AccessDeniedException;

    void delete(Integer id, User author) throws AccessDeniedException;
}
