package frgp.utn.edu.ar.quepasa.controller;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;

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
import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.service.Auth;
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
    private final Auth auth;

    @Autowired
    public PostController(PostService postService, AuthenticationService authenticationService, VoteService voteService, CommentService commentService, Auth auth) {
        this.postService = postService;
        this.authenticationService = authenticationService;
        this.voteService = voteService;
        this.commentService = commentService;
        this.auth = auth;
    }

    /**
     * Crea una publicacion nueva.
     *
     * @param post    Detalles de la publicacion a crear.
     * @return        Entidad de respuesta con los detalles de la publicacion creada.
     * @throws Fail   Si el usuario no esta  autenticado o no tiene permiso para hacerlo.
     * @throws ValidationError Si la publicacion no cumple con las validaciones.
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest post) {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.create(post, me));
    }

    /**
     * Recupera una lista paginada de publicaciones activas o inactivas, según sea especificado.
     *
     * @param page     Número de la página a obtener. Comienza en 0.
     * @param size     Tamaño de la página.
     * @param activeOnly Si se desean obtener solo las publicaciones activas.
     * @return          Página de publicaciones encontradas.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findAll(pageable, activeOnly));
    }

    /**
     * Recupera una lista paginada de publicaciones que coinciden con los criterios de búsqueda especificados.
     *
     * @param q El término de búsqueda que se usará para filtrar las publicaciones.
     * @param sort El criterio de ordenamiento para las publicaciones, con un valor predeterminado de "title,asc".
     * @param page El número de página a recuperar, con un valor predeterminado de 0.
     * @param size El número de publicaciones por página, con un valor predeterminado de 10.
     * @param active Indica si solo se deben recuperar publicaciones activas, con un valor predeterminado de true.
     * @return Un ResponseEntity que contiene la lista paginada de publicaciones filtradas.
     */
    @GetMapping("/search")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="title,asc") String sort, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(postService.search(q, pageable, active));
    }

    /**
     * Recupera una publicación por su identificador único.
     *
     * @param id El identificador único de la publicación a recuperar.
     * @return Un ResponseEntity que contiene la publicación correspondiente al identificador proporcionado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    /**
     * Recupera publicaciones creadas por un usuario específico (autor original).
     *
     * @param id El ID del autor original cuyas publicaciones se desean recuperar.
     * @param page El número de página a recuperar, con un valor predeterminado de 0.
     * @param size La cantidad de publicaciones por página, con un valor predeterminado de 10.
     * @return Un ResponseEntity que contiene la lista paginada de publicaciones del usuario especificado.
     */
    @GetMapping("/op/{id}")
    public ResponseEntity<?> getPostsByOp(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByOp(id, pageable));
    }

    /**
     * Recupera publicaciones filtradas por un tipo de audiencia especificado.
     *
     * @param audience El tipo de audiencia por el cual se filtrarán las publicaciones.
     * @param page El número de página a recuperar, el valor predeterminado es 0.
     * @param size La cantidad de publicaciones por página, el valor predeterminado es 10.
     * @return Un ResponseEntity que contiene una lista de publicaciones para la audiencia especificada.
     */
    @GetMapping("/audience/{audience}")
    public ResponseEntity<?> getPostsByAudience(@PathVariable Audience audience, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByAudience(audience, pageable));
    }

    /**
     * Recupera las publicaciones de un tipo especificado.
     *
     * @param id El id del tipo
     * @param page El número de página a recuperar
     * @param size La cantidad de elementos por página
     * @return Una lista paginada de publicaciones del tipo especificado
     */
    @GetMapping("/type/{id}")
    public ResponseEntity<?> getPostsByType(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByType(id, pageable));
    }

    /**
     * Recupera las publicaciones de un subtipo especificado.
     * 
     * @param id El id del subtipo
     * @param page El n mero de p gina a recuperar
     * @param size La cantidad de elementos por p gina
     * @return Una lista paginada de publicaciones del subtipo especificado
     */
    @GetMapping("/subtype/{id}")
    public ResponseEntity<?> getPostsBySubtype(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findBySubtype(id, pageable));
    }

    /** Comienza sección de FECHAS **/

    /**
     * Recupera las publicaciones dentro de un rango de fechas especificado.
     *
     * @param start la fecha de inicio del rango en formato "yyyy-MM-dd"
     * @param end la fecha de fin del rango en formato "yyyy-MM-dd"
     * @param page el número de página para la paginación (el valor predeterminado es 0)
     * @param size la cantidad de publicaciones por página (el valor predeterminado es 10)
     * @return un ResponseEntity que contiene una página de publicaciones dentro del rango de fechas,
     *         o una respuesta de solicitud incorrecta si el formato de la fecha es inválido
     */
    @GetMapping("/date/{start}/{end}")
    public ResponseEntity<?> getPostsByDateRange(@PathVariable String start, @PathVariable String end, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        try {
            Timestamp startTimestamp = Timestamp.valueOf(start + " 00:00:00");
            Timestamp endTimestamp = Timestamp.valueOf(end + " 23:59:59");

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(postService.findByDateRange(startTimestamp, endTimestamp, pageable));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }


    /**
     * Obtiene posts que comienzan después de una fecha específica.
     *
     * @param start la fecha de inicio en formato de cadena
     * @param page el número de página a obtener
     * @param size el número de elementos por página
     * @return una respuesta que contiene una página de posts que comienzan después de la fecha especificada
     */
    @GetMapping("/date-start/{start}")
    public ResponseEntity<?> getPostsByDateStart(@PathVariable String start, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        try {
            Timestamp startTimestamp = Timestamp.valueOf(start + " 00:00:00");

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(postService.findByDateStart(startTimestamp, pageable));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }

    /**
     * Obtiene posts que terminan antes de una fecha específica.
     *
     * @param end la fecha de fin
     * @param page la página a obtener
     * @param size el tamaño de la página
     * @return una página de posts que terminan antes de la fecha especificada
     */
    @GetMapping("/date-end/{end}")
    public ResponseEntity<?> getPostsByDateEnd(@PathVariable String end, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        try {
            Timestamp endTimestamp = Timestamp.valueOf(end + " 00:00:00");

            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(postService.findByDateEnd(endTimestamp, pageable));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid date format");
        }
    }
    // Termina sección de FECHAS **/

    /**
     * Obtiene las publicaciones del usuario autenticado.
     * @param page Número de página a obtener
     * @param size Número de elementos por página
     * @return Publicaciones del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getPostsByAuthUser(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        User me = authenticationService.getCurrentUserOrDie();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByOp(me.getId(), pageable));
    }

    /**
     * Edita una publicación.
     * @param id Identificador de la publicación a editar
     * @param post Información de la publicación a editar
     * @return La publicación editada
     * @throws AccessDeniedException si el usuario autenticado no es el due o de la publicación
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id, @RequestBody PostPatchEditRequest post) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.update(id, post, me));
    }

    /**
     * Elimina una publicación.
     * @param id Identificador de la publicación a eliminar
     * @return 204 No Content si se eliminó correctamente, 403 Forbidden si no se tiene permiso
     * @throws AccessDeniedException si no se tiene permiso para eliminar la publicación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        postService.delete(id, me);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    /** Comienza sección de VOTOS **/

    /**
     * Retorna la cuenta de votos de una publicación.
     * @param id Identificador de la publicación a obtener votos
     * @return La cuenta de votos actuales de la publicación
     */
    @GetMapping("/{id}/votes")
    public ResponseEntity<VoteCount> getVotes(@PathVariable Integer id) {
        return ResponseEntity.ok(voteService.count(Post.identify(id)));
    }

    /**
     * Incrementa el voto de una publicación especificada.
     * @param id Identificador del post a incrementar el voto
     * @return La nueva cuenta de votos después de incrementar
     */
    @PostMapping("/{id}/votes/up")
    public ResponseEntity<VoteCount> upVote(@PathVariable Integer id) {
        var post = Post.identify(id);
        var voteResult = voteService.vote(post, 1);
        return ResponseEntity.ok(voteResult);
    }

    /**
     * Decrementa el voto de una publicación especificada.
     * @param id Identificador del post a decrementar el voto
     * @return La nueva cuenta de votos después de decrementar
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
     * Comenta un post con el contenido dado.
     * @param id Identificador del post a comentar
     * @param content Contenido del comentario
     * @return El comentario creado
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> comment(@PathVariable Integer id, @RequestBody String content) {
        return ResponseEntity.ok(commentService.create(content, Post.identify(id)));
    }

    /**
     * Devuelve una página de comentarios de un post.
     * @param id Identificador del post
     * @param pageable Parámetros de paginación
     * @return Página de comentarios
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<PostComment>> viewComments(@PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(commentService.findAllFromPost(id, pageable));
    }
    // Termina sección de COMENTARIOS **/

    /**
     * Obtiene los posts de un usuario en su barrio.
     * @param userId Identificador del usuario
     * @param userBarrio Identificador del barrio del usuario
     * @return Lista de posts del usuario en su barrio
     */
    @GetMapping("/user/{userId}/barrio/{userBarrio}")
    public List<PostDTO> obtenerPosts(@PathVariable int userId, @PathVariable int userBarrio) {
        return postService.obtenerPosts(userBarrio, userId);
    }

}
