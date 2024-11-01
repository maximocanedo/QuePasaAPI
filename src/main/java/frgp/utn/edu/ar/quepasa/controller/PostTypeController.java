package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import quepasa.api.exceptions.ValidationError;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/post-types")
public class PostTypeController {

    private final PostTypeService postTypeService;
    private final AuthenticationService authenticationService;

    @Autowired
    public PostTypeController(PostTypeService postTypeService, AuthenticationService authenticationService) {
        this.postTypeService = postTypeService;
        this.authenticationService = authenticationService;
    }

    /**
     * Crea un tipo nuevo.
     *
     * @param description Descripción del tipo a crear.
     * @return Entidad de respuesta con los detalles del tipo creado.
     */
    @PostMapping
    public ResponseEntity<?> createPostType(@RequestBody String description) {
        return ResponseEntity.ok(postTypeService.create(description));
    }

    /**
     * Obtiene una lista paginada de tipos activos o inactivos, según sea especificado.
     *
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param activeOnly Si se desean obtener solo los tipos activos. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de tipos encontrados.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getPostTypes(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size,  @RequestParam(defaultValue="true") boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postTypeService.findAll(pageable, activeOnly));
    }

    /**
     * Obtiene una lista paginada de tipos que coinciden con los criterios de búsqueda especificados.
     *
     * @param q Parámetro de búsqueda que se usará para filtrar los tipos.
     * @param sort Parámetro de ordenamiento para los tipos, con un valor predeterminado de "description,asc".
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param active Si se desean obtener solo los tipos activos. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de tipos filtrados.
     */
    @GetMapping("/search")
    public ResponseEntity<?> getPostTypes(@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="description,asc") String sort, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(postTypeService.search(q, pageable, active));
    }

    /**
     * Obtiene un tipo según su ID.
     *
     * @param id ID del tipo a buscar.
     * @return Entidad de respuesta que contiene el tipo buscado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(postTypeService.findById(id));
    }

    /**
     * Edita un tipo.
     * @param id ID del tipo a editar.
     * @return Entidad de respuesta que contiene el tipo editado.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePostType(@PathVariable Integer id, @RequestBody String description) {
        return ResponseEntity.ok(postTypeService.update(id, description));
    }

    /**
     * Elimina un tipo.
     * @param id ID del tipo a eliminar.
     * @return Entidad de respuesta de tipo 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostType(@PathVariable Integer id) {
        postTypeService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
