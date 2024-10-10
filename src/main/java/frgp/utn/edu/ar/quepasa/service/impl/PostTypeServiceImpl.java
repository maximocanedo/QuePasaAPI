package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.service.PostTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service("postTypeService")
public class PostTypeServiceImpl implements PostTypeService {

    private final PostTypeRepository postTypeRepository;

    public PostTypeServiceImpl(PostTypeRepository postTypeRepository) {
        this.postTypeRepository = postTypeRepository;
    }

    @Override
    public Page<PostType> listPostTypes(Pageable pageable) { return postTypeRepository.findAll(pageable); }

    @Override
    public PostType findById(Integer id) {
        return postTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found"));
    }

    @Override
    public PostType create(String description) {
        PostType type = new PostType();
        type.setDescription(description);
        postTypeRepository.save(type);
        return type;
    }

    @Override
    public PostType update(Integer id, String description) {
        PostType type = findById(id);
        type.setDescription(description);
        postTypeRepository.save(type);
        return type;
    }

    @Override
    public void delete(Integer id) {
        PostType type = findById(id);
        type.setActive(false);
        postTypeRepository.save(type);
    }
}
