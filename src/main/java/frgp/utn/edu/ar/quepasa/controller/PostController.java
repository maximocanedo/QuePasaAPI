package frgp.utn.edu.ar.quepasa.controller;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.response.PostDTO;
import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import frgp.utn.edu.ar.quepasa.service.PostService;
import frgp.utn.edu.ar.quepasa.service.VoteService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final AuthenticationService authenticationService;
    private final VoteService voteService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, AuthenticationService authenticationService, VoteService voteService, CommentService commentService) {
        this.postService = postService;
        this.authenticationService = authenticationService;
        this.voteService = voteService;
        this.commentService = commentService;
    }

    /**
     * Crea una publicación nueva.
     *
     * @param post Detalles de la publicación a crear.
     * @return Entidad de respuesta con los detalles de la publicación creada.
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostCreateRequest post) {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.create(post, me));
    }

    /**
     * Obtiene una lista paginada de publicaciones activas o inactivas, según sea especificado.
     *
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param activeOnly Si se desean obtener solo las publicaciones activas. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de publicaciones encontradas.
     */
    @GetMapping("/all")
    public ResponseEntity<Page<Post>> getPosts(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findAll(pageable, activeOnly));
    }

    /**
     * Obtiene una lista paginada de publicaciones que coinciden con los criterios de búsqueda especificados.
     *
     * @param q Parámetro de búsqueda que se usará para filtrar las publicaciones.
     * @param sort Parámetro de ordenamiento para las publicaciones, con un valor predeterminado de "title,asc".
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @param active Si se desean obtener solo las publicaciones activas. Valor predeterminado es true.
     * @return Entidad de respuesta con una lista paginada de publicaciones filtradas.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Post>> getPosts(@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="title,asc") String sort, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(postService.search(q, pageable, active));
    }

    /**
     * Obtiene una publicación según su ID.
     *
     * @param id ID de la publicación a buscar.
     * @return Entidad de respuesta que contiene la publicación buscada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    /**
     * Obtiene publicaciones que pertenecen a un OP (autor).
     *
     * @param id ID del OP.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones del usuario especificado.
     */
    @GetMapping("/op/{id}")
    public ResponseEntity<Page<Post>> getPostsByOp(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByOp(id, pageable));
    }

    /**
     * Obtiene publicaciones según su audiencia.
     *
     * @param audience Audiencia de las publicaciones.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones con la audiencia especificada.
     */
    @GetMapping("/audience/{audience}")
    public ResponseEntity<Page<Post>> getPostsByAudience(@PathVariable Audience audience, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByAudience(audience, pageable));
    }

    /**
     * Obtiene las publicaciones de un tipo especificado.
     *
     * @param id ID del tipo.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones con el tipo especificado.
     */
    @GetMapping("/type/{id}")
    public ResponseEntity<Page<Post>> getPostsByType(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByType(id, pageable));
    }

    /**
     * Obtiene las publicaciones de un subtipo especificado.
     * 
     * @param id ID del subtipo.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones con el subtipo especificado.
     */
    @GetMapping("/subtype/{id}")
    public ResponseEntity<Page<Post>> getPostsBySubtype(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findBySubtype(id, pageable));
    }

    /** Comienza sección de FECHAS **/
    /**
     * Obtiene las publicaciones dentro de un rango de fechas especificado.
     *
     * @param start Fecha de inicio del rango en formato "yyyy-MM-dd"
     * @param end Fecha de fin del rango en formato "yyyy-MM-dd"
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones dentro del rango de fechas,
     * o una respuesta de tipo Bad Request indicando que el formato de fecha es inválido.
     */
    @GetMapping("/date/{start}/{end}")
    public ResponseEntity<Page<Post>> getPostsByDateRange(@PathVariable String start, @PathVariable String end, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        try {
            Timestamp startTimestamp = Timestamp.valueOf(start + " 00:00:00");
            Timestamp endTimestamp = Timestamp.valueOf(end + " 23:59:59");

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(postService.findByDateRange(startTimestamp, endTimestamp, pageable));
        }
        catch (IllegalArgumentException e) {
            throw new Fail("Invalid date format", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene las publicaciones que comienzan después de una fecha específica.
     *
     * @param start Fecha de inicio en formato String
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones después de la fecha de inicio.
     */
    @GetMapping("/date-start/{start}")
    public ResponseEntity<Page<Post>> getPostsByDateStart(@PathVariable String start, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        try {
            Timestamp startTimestamp = Timestamp.valueOf(start + " 00:00:00");

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(postService.findByDateStart(startTimestamp, pageable));
        }
        catch (IllegalArgumentException e) {
            throw new Fail("Invalid date format", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene las publicaciones que comienzan antes de una fecha específica.
     *
     * @param end Fecha de fin en formato String
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones antes de la fecha de fin.
     */
    @GetMapping("/date-end/{end}")
    public ResponseEntity<Page<Post>> getPostsByDateEnd(@PathVariable String end, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        try {
            Timestamp endTimestamp = Timestamp.valueOf(end + " 00:00:00");

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(postService.findByDateEnd(endTimestamp, pageable));
        }
        catch (IllegalArgumentException e) {
            throw new Fail("Invalid date format", HttpStatus.BAD_REQUEST);
        }
    }
    // Termina sección de FECHAS **/

    /**
     * Obtiene las publicaciones del usuario autenticado.
     * @param page Número de páginas a obtener. Comienza en 0.
     * @param size Tamaño de cada página. Comienza en 10.
     * @return Entidad de respuesta que contiene la lista paginada de publicaciones del usuario autenticado.
     */
    @GetMapping("/me")
    public ResponseEntity<Page<Post>> getPostsByAuthUser(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        User me = authenticationService.getCurrentUserOrDie();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByOp(me.getId(), pageable));
    }

    /**
     * Edita una publicación.
     * @param id ID de la publicación a editar.
     * @param post Nueva publicación.
     * @return Entidad de respuesta que contiene la publicación editada.
     * @throws AccessDeniedException si:
     * -El usuario no es el dueño de la publicación.
     * -El usuario no es administrador.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Integer id, @RequestBody PostPatchEditRequest post) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.update(id, post, me));
    }

    /**
     * Elimina una publicación.
     * @param id ID de la publicación a eliminar.
     * @return Entidad de respuesta de tipo 204 (No Content).
     * @throws AccessDeniedException si:
     * -El usuario no es el dueño de la publicación.
     * -El usuario no es administrador.
     * -El usuario no es moderador.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        postService.delete(id, me);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /** Comienza sección de VOTOS **/
    /**
     * Devuelve la cuenta de votos de una publicación.
     * @param id ID de la publicación a obtener votos.
     * @return Entidad de respuesta con la cuenta de votos de la publicación.
     */
    @GetMapping("/{id}/votes")
    public ResponseEntity<VoteCount> getVotes(@PathVariable Integer id) {
        return ResponseEntity.ok(voteService.count(Post.identify(id)));
    }

    /**
     * Incrementa el voto de una publicación especificada.
     * @param id ID de la publicación a votar.
     * @return Entidad de respuesta con la cuenta de votos actualizada.
     */
    @PostMapping("/{id}/votes/up")
    public ResponseEntity<VoteCount> upVote(@PathVariable Integer id) {
        var post = Post.identify(id);
        var voteResult = voteService.vote(post, 1);
        return ResponseEntity.ok(voteResult);
    }

    /**
     * Decrementa el voto de una publicación especificada.
     * @param id ID de la publicación a decrementar voto.
     * @return Entidad de respuesta con la cuenta de votos actualizada.
     */
    @PostMapping("/{id}/votes/down")
    public ResponseEntity<VoteCount> downVote(@PathVariable Integer id) {
        return ResponseEntity.ok(voteService.vote(Post.identify(id), -1));
    }
    // Termina sección de VOTOS **/

    /**
     * Comienza sección de COMENTARIOS
     */
    /**
     * Comenta una publicación con el contenido dado.
     * @param id ID de la publicación a comentar.
     * @param content Contenido del comentario.
     * @return Entidad de respuesta con el nuevo comentario.
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> comment(@PathVariable Integer id, @RequestBody String content) {
        return ResponseEntity.ok(commentService.create(content, Post.identify(id)));
    }

    /**
     * Devuelve una página de comentarios de una publicación.
     * @param id ID de la publicación.
     * @param pageable Parámetros de paginación.
     * @return Entidad de respuesta con la lista paginada de comentarios.
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> viewComments(@PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(commentService.findAllFromPost(id, pageable).map(voteService::populate));
    }
    // Termina sección de COMENTARIOS **/

    /**
     * Obtiene las publicaciones de un usuario en su barrio.
     * @param userId ID del usuario.
     * @param userNeighbourhood ID del barrio del usuario.
     * @return Lista de posts del usuario en su barrio
     */
    @GetMapping("/user/{userId}/neighbourhood/{userNeighbourhood}")
    public ResponseEntity<List<PostDTO>> getPosts(@PathVariable int userId, @PathVariable int userNeighbourhood) {
        return ResponseEntity.ok(postService.findPosts(userId, userNeighbourhood));
    }

}
