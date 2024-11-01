package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("postSubtypeService")
public class PostSubtypeServiceImpl implements PostSubtypeService {

    private final PostSubtypeRepository postSubtypeRepository;
    private final PostTypeRepository postTypeRepository;

    @Autowired
    public PostSubtypeServiceImpl(PostSubtypeRepository postSubtypeRepository, PostTypeRepository postTypeRepository) {
        this.postSubtypeRepository = postSubtypeRepository;
        this.postTypeRepository = postTypeRepository;
    }

    /**
     * Busca todos los subtipos que contengan el texto dado en la descripción,
     * activos o no activos, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     *
     * @param q El texto que se busca en el título y descripción de los subtipos
     * @param pageable El objeto que contiene la información de la paginación
     * @param active Indica si se quieren obtener solo los subtipos activos o no
     * @return La lista de subtipos paginada
     */
    @Override
    public Page<PostSubtype> search(String q, Pageable pageable, boolean active) {
        return postSubtypeRepository.search(q, pageable, active);
    }

    /**
     * Busca todos los subtipos, activos o no activos, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     *
     * @param pageable El objeto que contiene la información de la paginación
     * @param activeOnly Indica si se quieren obtener solo los subtipos activos o no
     * @return La lista de subtipos paginada
     */
    @Override
    public Page<PostSubtype> findAll(Pageable pageable, boolean activeOnly) {
        if(activeOnly) {
            return postSubtypeRepository.findAllActive(pageable);
        }
        return postSubtypeRepository.findAll(pageable);
    }

    /**
     * Busca un subtipo por su ID y lo devuelve.
     *
     * @param id El ID del subtipo que se busca
     * @return El subtipo encontrado
     * @throws Fail Si el subtipo no es encontrado
     */
    @Override
    public PostSubtype findById(Integer id) {
        return postSubtypeRepository.findActiveById(id)
                .orElseThrow(() -> new Fail("Subtype not found", HttpStatus.NOT_FOUND));
    }

    /**
     * Obtiene una página de subtipos que pertenecen a un tipo específico.
     *
     * @param pageable La información de paginación
     * @param type El tipo que se busca
     * @return Una página de subtipos que pertenecen al tipo buscado
     * @throws Fail Si el tipo no es encontrado
     */
    @Override
    public Page<PostSubtype> findByType(Integer type, Pageable pageable) {
        PostType postType = postTypeRepository.findById(type)
                .orElseThrow(() -> new Fail("Type not found", HttpStatus.NOT_FOUND));
        return postSubtypeRepository.findByType(postType, pageable);
    }

    /**
     * Crea un nuevo subtipo.
     *
     * El usuario actual debe ser administrador.
     *
     * @param newSubtype El nuevo subtipo
     * @return El subtipo creado
     */
    @Override
    public PostSubtype create(PostSubtypeRequest newSubtype) {
        PostSubtype subtype = new PostSubtype();
        var type = postTypeRepository.findActiveById(newSubtype.getType())
                .orElseThrow(() -> new Fail("Type not found. ", HttpStatus.BAD_REQUEST));
        subtype.setType(type);
        subtype.setDescription(newSubtype.getDescription());
        postSubtypeRepository.save(subtype);
        return subtype;
    }

    /**
     * Actualiza un subtipo.
     *
     * El usuario actual debe ser administrador.
     *
     * @param id El ID del subtipo
     * @param newSubtype El nuevo subtipo
     * @return El subtipo actualizado
     */
    @Override
    public PostSubtype update(Integer id, PostSubtypeRequest newSubtype) {
        PostSubtype subtype = findById(id);
        var type = postTypeRepository.findActiveById(newSubtype.getType())
                .orElseThrow(() -> new Fail("Type not found. ", HttpStatus.BAD_REQUEST));
        subtype.setType(type);
        subtype.setDescription(newSubtype.getDescription());
        postSubtypeRepository.save(subtype);
        return subtype;
    }

    /**
     * Elimina un subtipo.
     *
     * Sólo un administrador puede eliminarlo.
     *
     * @param id El ID del subtipo a eliminar
     */
    @Override
    public void delete(Integer id) {
        PostSubtype subtype = postSubtypeRepository
                .findById(id)
                .orElseThrow(() -> new Fail("Subtype not found. ", HttpStatus.NOT_FOUND));
        subtype.setActive(false);
        postSubtypeRepository.save(subtype);
    }
}
