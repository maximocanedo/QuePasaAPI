package frgp.utn.edu.ar.quepasa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;


@RestController
@RequestMapping("/api/post-subtypes")
public class PostSubtypeController {

    private final PostSubtypeService postSubtypeService;

    private final AuthenticationService authenticationService;

    @Autowired
    public PostSubtypeController(PostSubtypeService postSubtypeService, AuthenticationService authenticationService) {
        this.postSubtypeService = postSubtypeService;
        this.authenticationService = authenticationService;
    }

    /**
     * Crea un subtipo nuevo.
     *
     * @param subtype Detalles del subtipo a crear.
     * @return Entidad de respuesta con los detalles del subtipo creado.
     */
    @PostMapping
    public ResponseEntity<?> createPostSubtype(@RequestBody PostSubtypeRequest subtype) {
        return ResponseEntity.ok(postSubtypeService.create(subtype));
    }

    /**
     * Obtiene una lista paginada de subtipos activos o inactivos, según sea especificado.
     *
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param activeOnly Si se desean obtener solo los subtipos activos. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de subtipos encontrados.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getPostSubtypes(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postSubtypeService.findAll(pageable, activeOnly));
    }

    /**
     * Obtiene una lista paginada de subtipos que coinciden con los criterios de búsqueda especificados.
     *
     * @param q Parámetro de búsqueda que se usará para filtrar los subtipos.
     * @param sort Parámetro de ordenamiento para los subtipos, con un valor predeterminado de "description,asc".
     * @param page Número de páginas a obtener. Comienza en 0. 
     * @param size Tamaño de cada página. Comienza en 10.
     * @param active Si se desean obtener solo los subtipos activos. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de subtipos filtrados.
     */
    @GetMapping("/search")
    public ResponseEntity<?> getPostTypes(@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="description,asc") String sort, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(postSubtypeService.search(q, pageable, active));
    }

    /**
     * Obtiene un subtipo según su ID.
     *
     * @param id ID del subtipo a buscar.
     * @return Entidad de respuesta que contiene el subtipo buscado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostSubtypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(postSubtypeService.findById(id));
    }

    /**
     * Obtiene subtipos que pertenecen a un tipo.
     *
     * @param id ID del tipo.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de subtipos del tipo especificado.
     */
    @GetMapping("/type/{id}")
    public ResponseEntity<?> getPostSubtypesByType(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postSubtypeService.findByType(id, pageable));
    }

    /**
     * Edita un subtipo.
     * @param id ID del subtipo a editar.
     * @return Entidad de respuesta que contiene el subtipo editado.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePostSubtype(@PathVariable Integer id, @RequestBody PostSubtypeRequest subtype) {
        return ResponseEntity.ok(postSubtypeService.update(id, subtype));
    }

    /**
     * Elimina un subtipo.
     * @param id ID del subtipo a eliminar.
     * @return Entidad de respuesta de tipo 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostSubtype(@PathVariable Integer id)  {
        postSubtypeService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
