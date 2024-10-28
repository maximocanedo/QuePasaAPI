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

import java.nio.file.AccessDeniedException;

@Service("postTypeService")
public class PostTypeServiceImpl implements PostTypeService {

    private final PostTypeRepository postTypeRepository;

    @Autowired
    public PostTypeServiceImpl(PostTypeRepository postTypeRepository) {
        this.postTypeRepository = postTypeRepository;
    }

    /**
     * Busca todos los tipos que contengan el texto dado en la descripción,
     * activos o no activos, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     *
     * @param q El texto que se busca en el título y descripción de las publicaciones
     * @param pageable El objeto que contiene la información de la paginación
     * @param active Indica si se quieren obtener solo los tipos activos o no
     * @return La lista de tipos paginada
     */
    @Override
    public Page<PostType> search(String q, Pageable pageable, boolean active) {
        return postTypeRepository.search(q, pageable, active);
    }

    /**
     * Busca todos los tipos, activos o no activos, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     *
     * @param pageable El objeto que contiene la información de la paginación
     * @param activeOnly Indica si se quieren obtener solo los tipos activas o no
     * @return La lista de tipos paginada
     */
    @Override
    public Page<PostType> findAll(Pageable pageable, boolean activeOnly) {
        if(activeOnly) {
            return postTypeRepository.findAllActive(pageable);
        }
        return postTypeRepository.findAll(pageable);
    }

    /**
     * Busca un tipo por su ID y lo devuelve.
     *
     * @param id El ID del tipo que se busca
     * @return El tipo encontrado
     * @throws Fail Si el tipo no es encontrado
     */
    @Override
    public PostType findById(Integer id) {
        return postTypeRepository.findById(id)
                .orElseThrow(() -> new Fail("Type not found", HttpStatus.NOT_FOUND));
    }

    /**
     * Crea un nuevo tipo.
     *
     * El usuario actual debe ser administrador.
     *
     * @param description La descripción del nuevo tipo
     * @return El tipo creado
     */
    @Override
    public PostType create(String description) {
        PostType type = new PostType();
        type.setDescription(description);
        postTypeRepository.save(type);
        return type;
    }

    /**
     * Actualiza un tipo.
     *
     * El usuario actual debe ser administrador.
     *
     * @param id El ID del tipo
     * @param description La descripción del tipo
     * @return El tipo actualizado
     */
    @Override
    public PostType update(Integer id, String description) {
        PostType type = findById(id);
        type.setDescription(description);
        postTypeRepository.save(type);
        return type;
    }

    /**
     * Elimina un tipo.
     *
     * Sólo un administrador puede eliminarlo.
     *
     * @param id El ID del tipo a eliminar
     */
    @Override
    public void delete(Integer id) {
        PostType type = findById(id);
        type.setActive(false);
        postTypeRepository.save(type);
    }
}
