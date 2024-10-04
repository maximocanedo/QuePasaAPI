package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("postSubtypeService")
public class PostSubtypeServiceImpl implements PostSubtypeService {

    @Autowired
    private PostSubtypeRepository postSubtypeRepository;

    @Override
    public Page<PostSubtype> listPostSubtypes(Pageable pageable) { return postSubtypeRepository.findAll(pageable); }
}
