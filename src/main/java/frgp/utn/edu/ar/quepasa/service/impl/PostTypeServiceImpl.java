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

    @Autowired
    private PostTypeRepository postTypeRepository;

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
    public PostType update(Integer id, String description, User author) throws AccessDeniedException {
        PostType type = findById(id);
        if(!author.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        type.setDescription(description);
        postTypeRepository.save(type);
        return type;
    }

    @Override
    public void delete(Integer id, User author) throws AccessDeniedException {
        PostType type = findById(id);
        if(!author.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        type.setActive(false);
        postTypeRepository.save(type);
    }
}
