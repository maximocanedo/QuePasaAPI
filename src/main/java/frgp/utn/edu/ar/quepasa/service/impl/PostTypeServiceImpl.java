package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.service.PostTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("postTypeService")
public class PostTypeServiceImpl implements PostTypeService {

    private final PostTypeRepository postTypeRepository;

    @Autowired
    public PostTypeServiceImpl(PostTypeRepository postTypeRepository) {
        this.postTypeRepository = postTypeRepository;
    }

    @Override
    public Page<PostType> search(String q, Pageable pageable, boolean active) {
        return postTypeRepository.search(q, pageable, active);
    }

    @Override
    public Page<PostType> findAll(Pageable pageable, boolean activeOnly) {
        if(activeOnly) {
            return postTypeRepository.findAllActive(pageable);
        }
        return postTypeRepository.findAll(pageable);
    }

    @Override
    public PostType findById(Integer id) {
        return postTypeRepository.findById(id)
                .orElseThrow(() -> new Fail("Type not found", HttpStatus.NOT_FOUND));
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
