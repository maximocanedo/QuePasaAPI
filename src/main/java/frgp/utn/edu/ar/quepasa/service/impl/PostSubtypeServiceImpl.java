package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

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
                .orElseThrow(() -> new RuntimeException("Subtype not found"));
    }

    @Override
    public Page<PostSubtype> findByType(Integer type, Pageable pageable) {
        PostType postType = postTypeRepository.findById(type)
                .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
        return postSubtypeRepository.findByType(postType, pageable);
    }

    @Override
    public PostSubtype create(PostSubtypeRequest newSubtype) {
        PostSubtype subtype = new PostSubtype();
        PostType type = postTypeRepository.findById(newSubtype.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
        subtype.setType(type);
        subtype.setDescription(newSubtype.getDescription());
        postSubtypeRepository.save(subtype);
        return subtype;
    }

    @Override
    public PostSubtype update(Integer id, PostSubtypeRequest newSubtype, User author) throws AccessDeniedException {
        PostSubtype subtype = findById(id);
        if(!author.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        PostType type = postTypeRepository.findById(newSubtype.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
        subtype.setType(type);
        subtype.setDescription(newSubtype.getDescription());
        postSubtypeRepository.save(subtype);
        return subtype;
    }

    @Override
    public void delete(Integer id, User author) throws AccessDeniedException {
        PostSubtype subtype = findById(id);
        if(!author.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        subtype.setActive(false);
        postSubtypeRepository.save(subtype);
    }
}
