package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSubtypeService {
    Page<PostSubtype> listPostSubtypes(Pageable pageable);
}
