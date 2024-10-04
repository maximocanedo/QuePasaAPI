package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service("postSubtypeService")
public class PostSubtypeServiceImpl implements PostSubtypeService {

    @Autowired
    private PostSubtypeRepository postSubtypeRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Override
    public Page<PostSubtype> listPostSubtypes(Pageable pageable) { return postSubtypeRepository.findAll(pageable); }

    @Override
    public PostSubtype findById(Integer id) {
        return postSubtypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PostSubtype not found"));
    }

    @Override
    public Page<PostSubtype> findByPostType(Integer type, Pageable pageable) {
        PostType postType = postTypeRepository.findById(type)
                .orElseThrow(() -> new ResourceNotFoundException("PostType not found"));
        return postSubtypeRepository.findByPostType(postType, pageable);
    }
}
