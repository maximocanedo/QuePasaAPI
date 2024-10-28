package frgp.utn.edu.ar.quepasa.service.impl;


import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.response.PostDTO;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.PostService;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import frgp.utn.edu.ar.quepasa.service.validators.objects.NeighbourhoodValidator;
import frgp.utn.edu.ar.quepasa.service.validators.objects.PostSubtypeValidator;


@Service("postService")
public class PostServiceImpl implements PostService {

    private final OwnerService ownerService;
    private final VoteService voteService;
    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    private final PostSubtypeRepository postSubtypeRepository;
    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private CommentService commentService;

    @Autowired
    public PostServiceImpl(
            OwnerService ownerService, VoteService voteService,
            PostRepository postRepository,
            PostTypeRepository postTypeRepository,
            PostSubtypeRepository postSubtypeRepository,
            UserRepository userRepository,
            NeighbourhoodRepository neighbourhoodRepository
    ) {
        this.ownerService = ownerService;
        this.voteService = voteService;
        this.postRepository = postRepository;
        this.postTypeRepository = postTypeRepository;
        this.postSubtypeRepository = postSubtypeRepository;
        this.userRepository = userRepository;
        this.neighbourhoodRepository = neighbourhoodRepository;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Busca todos los posts que contengan el texto dado en el t tulo o
     * descripci n, activos o no activos, de acuerdo al par metro given.
     * La lista se devuelve paginada seg n el objeto Pageable dado.
     * Antes de devolver la lista, se pobla con la informaci n de votos y
     * comentarios correspondiente a cada post.
     *
     * @param q El texto que se busca en el t tulo y descripci n de los posts
     * @param pageable El objeto que contiene la informaci n de paginaci n
     * @param activeOnly Indica si se quieren obtener solo los posts activos o no
     */
    @Override
    public Page<Post> search(String q, Pageable pageable, boolean active) {
        return postRepository
                .search(q, pageable, active)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Busca todos los posts, activos o no activos, de acuerdo al par metro given.
     * La lista se devuelve paginada seg n el objeto Pageable dado.
     * Antes de devolver la lista, se pobla con la informaci n de votos y comentarios
     * correspondiente a cada post.
     *
     * @param pageable El objeto que contiene la informaci n de paginaci n
     * @param activeOnly Indica si se quieren obtener solo los posts activos o no
     * @return La lista de posts paginada con su informaci n de votos y comentarios
     */
    @Override
    public Page<Post> findAll(Pageable pageable, boolean activeOnly) {
        if(activeOnly) {
            return postRepository
                    .findAllActive(pageable)
                    .map(voteService::populate)
                    .map(commentService::populate);
        }
        return postRepository
                .findAll(pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Busca un post por su ID y lo devuelve con los comentarios y votos poblados.
     *
     * @param id el ID del post que se busca
     * @return el post encontrado con comentarios y votos poblados
     * @throws Fail si el post no es encontrado
     */
    @Override
    public Post findById(Integer id) {
        return commentService.populate(
            voteService.populate(
                postRepository
                    .findById(id)
                    .orElseThrow(() -> new Fail("Post not found", HttpStatus.NOT_FOUND))
            )
        );
    }

    /**
     * Obtiene una página de posts que pertenecen a un OP específico.
     *
     * @param originalPoster el ID del usuario que se busca
     * @param pageable la información de paginación
     * @return una página de posts que pertenecen al OP dada
     * @throws Fail si el OP no es encontrada
     */
    @Override
    public Page<Post> findByOp(Integer originalPoster, Pageable pageable) {
        User user = userRepository.findById(originalPoster)
                .orElseThrow(() -> new Fail("User not found", HttpStatus.NOT_FOUND));
        return postRepository
                .findByOwner(user, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene una página de posts que pertenecen a una audiencia específica.
     *
     * @param audience la audiencia que se busca
     * @param pageable la información de paginación
     * @return una página de posts que pertenecen a la audiencia dada
     * @throws Fail si la audiencia no es encontrada
     */
    @Override
    public Page<Post> findByAudience(Audience audience, Pageable pageable) {
        return postRepository
                .findByAudience(audience, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene una página de posts que pertenecen a un tipo específico.
     *
     * @param type el ID del tipo de post
     * @param pageable la información de paginación
     * @return una página de posts que pertenecen al tipo dado
     * @throws Fail si el tipo no es encontrado
     */
    @Override
    public Page<Post> findByType(Integer type, Pageable pageable) {
        PostType postType = postTypeRepository.findById(type)
                .orElseThrow(() -> new Fail("Type not found", HttpStatus.NOT_FOUND));
        return postRepository
                .findByType(postType.getId(), pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
    * Obtiene una página de posts relacionados a un subtipo específico.
    *
    * @param subtype el ID del subtipo de post
    * @param pageable la información de paginación
    * @return una página de posts que pertenecen al subtipo dado
    * @throws Fail si el subtipo no es encontrado
    */
    @Override
    public Page<Post> findBySubtype(Integer subtype, Pageable pageable) {
        PostSubtype postSubtype = postSubtypeRepository.findById(subtype)
                .orElseThrow(() -> new Fail("Subtype not found", HttpStatus.NOT_FOUND));
        return postRepository
                .findBySubtype(postSubtype.getId(), pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene posts que se encuentran dentro del rango de fechas indicado.
     *
     * @param start fecha de inicio del rango (inclusive)
     * @param end   fecha de fin del rango (inclusive)
     * @return      una página de posts que se encuentran en el rango de fechas
     */
    @Override
    public Page<Post> findByDateRange(Timestamp start, Timestamp end, Pageable pageable) {
        return postRepository
                .findByDateRange(start, end, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene posts que comienzan después de una fecha específica.
     *
     * @param start la fecha de inicio
     * @param pageable la paginación
     * @return una página de posts que comienzan después de la fecha especificada
     */
    @Override
    public Page<Post> findByDateStart(Timestamp start, Pageable pageable) {
        return postRepository
                .findByDateStart(start, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene posts que terminan en un rango de fechas
     *
     * @param end la fecha de fin del rango
     * @param pageable la paginación
     * @return una página de posts que terminan en el rango de fechas
     */
    @Override
    public Page<Post> findByDateEnd(Timestamp end, Pageable pageable) {
        return postRepository
                .findByDateEnd(end, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Crea un nuevo post.
     *
     * El usuario actual debe ser el dueño del post.
     *
     * @param newPost el nuevo post
     * @param originalPoster el usuario que hace la petición
     * @return el post creado
     */
    @Override
    public Post create(PostCreateRequest newPost, User originalPoster) {
        Post post = new Post();
        post.setOwner(originalPoster);
        post.setAudience((newPost.getAudience()));
        post.setTitle(newPost.getTitle());
        var subtype = new PostSubtypeValidator(newPost.getSubtype(), postSubtypeRepository)
                .isActive(postSubtypeRepository)
                .build();
        post.setSubtype(subtype);
        post.setDescription(newPost.getDescription());
        var n = neighbourhoodRepository
                .findActiveNeighbourhoodById(newPost.getNeighbourhood())
                .orElseThrow(() -> new Fail("Neighbourhood not found. ", HttpStatus.BAD_REQUEST));
        var neighbourhood = new NeighbourhoodValidator(n)
                .isActive(neighbourhoodRepository)
                .build();
        post.setNeighbourhood(neighbourhood);
        post.setTimestamp(newPost.getTimestamp());
        post.setTags(newPost.getTags());
        postRepository.save(post);
        return commentService.populate(voteService.populate(post));
    }

    /**
     * Actualiza un post.
     *
     * El usuario actual debe ser el dueño administrador
     * para poder actualizar el post.
     *
     * @param id el id del post
     * @param newPost el nuevo contenido
     * @param originalPoster el usuario que realiz  la actualizacion
     * @return el post actualizado
     * @throws AccessDeniedException si el usuario no es el dueño administrador del post
     */
    @Override
    public Post update(Integer id, PostPatchEditRequest newPost, User originalPoster) throws AccessDeniedException {
        Post post = findById(id);
        ownerService.of(post)
                .isOwner()
                .isAdmin()
                .orElseFail();
        if(newPost.getTitle() != null) post.setTitle(newPost.getTitle());
        if(newPost.getSubtype() != null) {
            var subtype = new PostSubtypeValidator(newPost.getSubtype(), postSubtypeRepository)
                    .isActive(postSubtypeRepository)
                    .build();
            post.setSubtype(subtype);
        }
        if(newPost.getDescription() != null) post.setDescription(newPost.getDescription());
        if(newPost.getNeighbourhood() != null) {
            var n = neighbourhoodRepository
                    .findActiveNeighbourhoodById(newPost.getNeighbourhood())
                    .orElseThrow(() -> new Fail("Neighbourhood not found. ", HttpStatus.BAD_REQUEST));
            var neighbourhood = new NeighbourhoodValidator(n)
                    .isActive(neighbourhoodRepository)
                    .build();
            post.setNeighbourhood(neighbourhood);
        }
        if(newPost.getTags() != null) post.setTags(newPost.getTags());
        postRepository.save(post);
        return commentService.populate(voteService.populate(post));
    }

    /**
     * Elimina un post.
     * Sólo el propietario del post, el administrador o el moderador del barrio
     * del post pueden eliminarlo.
     *
     * @param id el ID del post a eliminar
     * @param originalPoster el usuario que intenta eliminar el post
     * @throws AccessDeniedException si el usuario no tiene permiso para eliminar
     * el post
     */
    @Override
    public void delete(Integer id, User originalPoster) throws AccessDeniedException {
        Post post = findById(id);
        ownerService.of(post)
                .isOwner()
                .isAdmin()
                .isModerator()
                .orElseFail();
        post.setActive(false);
        postRepository.save(post);
    }

    /**
     * Ejecuta el SP "obtenerPosts" y devuelve una lista de DTOs de posts.
     *
     * @param userBarrio el barrio del usuario logueado
     * @param userId     el ID del usuario logueado
     * @return una lista de DTOs de posts
     */
    @Override
    public List<PostDTO> obtenerPosts(int userBarrio, int userId) {
        List<Map<String, Object>> rawResults = postRepository.obtenerPosts(userBarrio, userId);
        return rawResults.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Convierte una fila de resultados de la consulta a un DTO de Post.
     *
     * @param row la fila de resultados
     * @return el DTO de Post
     */
    private PostDTO mapToDTO(Map<String, Object> row) {
        PostDTO dto = new PostDTO();
        dto.setId((Integer) row.get("id"));
        dto.setActive((Boolean) row.get("active"));
        dto.setAudience((String) row.get("audience"));
        dto.setDescription((String) row.get("description"));
        dto.setTags((String) row.get("tags"));
        dto.setTimestamp((Timestamp) row.get("timestamp"));
        dto.setTitle((String) row.get("title"));
        dto.setNeighbourhood((Integer) row.get("neighbourhood"));
        dto.setOp((Integer) row.get("op"));
        dto.setSubtype((Integer) row.get("subtype"));
        dto.setPostTypeDescription((String) row.get("post_type_description"));
        dto.setPostSubtypeDescription((String) row.get("post_subtype_description"));
        dto.setTotalVotes((Integer) row.get("total_votes"));
        dto.setUserVotes((Integer) row.get("user_votes"));
        dto.setCommentId((Integer) row.get("comment_id"));
        dto.setPictureId((Integer) row.get("picture_id"));
        dto.setPictureDescription((String) row.get("picture_description"));
        dto.setPictureUploadedAt((Timestamp) row.get("picture_uploaded_at"));
        dto.setPictureMediaType((String) row.get("picture_media_type"));
        dto.setScore((Integer) row.get("score"));

        return dto;
    }

}
