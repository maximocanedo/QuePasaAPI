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
     * Busca todas las publicaciones que contengan el texto dado en el título o
     * descripción, activas o no activas, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     * Antes de devolver la lista, se pobla con la información de votos y
     * comentarios correspondiente a cada publicación.
     *
     * @param q El texto que se busca en el título y descripción de las publicaciones
     * @param pageable El objeto que contiene la información de la paginación
     * @param active Indica si se quieren obtener solo las publicaciones activas o no
     * @return La lista de publicaciones paginada con su información de votos y comentarios
     */
    @Override
    public Page<Post> search(String q, Pageable pageable, boolean active) {
        return postRepository
                .search(q, pageable, active)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Busca todas los publicaciones, activas o no activas, de acuerdo al parámetro dado.
     * La lista se devuelve paginada según el objeto Pageable dado.
     * Antes de devolver la lista, se pobla con la información de votos y comentarios
     * correspondiente a cada publicación.
     *
     * @param pageable El objeto que contiene la información de la paginación
     * @param activeOnly Indica si se quieren obtener solo las publicaciones activas o no
     * @return La lista de publicaciones paginada con su información de votos y comentarios
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
     * Busca una publicación por su ID y lo devuelve con los comentarios y votos poblados.
     *
     * @param id El ID de la publicación que se busca
     * @return La publicación encontrada con comentarios y votos poblados
     * @throws Fail Si la publicación no es encontrada
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
     * Obtiene una página de publicaciones que pertenecen a un OP (autor) específico.
     *
     * @param originalPoster El ID del usuario que se busca
     * @param pageable La información de paginación
     * @return Una página de publicaciones que pertenecen al OP buscado
     * @throws Fail Si el OP no es encontrado
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
     * @param audience La audiencia que se busca
     * @param pageable La información de paginación
     * @return Una página de publicaciones que pertenecen a la audiencia buscada
     * @throws Fail Si la audiencia no es encontrada
     */
    @Override
    public Page<Post> findByAudience(Audience audience, Pageable pageable) {
        return postRepository
                .findByAudience(audience, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene una página de publicaciones que pertenecen a un tipo específico.
     *
     * @param type El ID del tipo de publicación
     * @param pageable La información de paginación
     * @return Una página de publicaciones que pertenecen al tipo buscado
     * @throws Fail Si el tipo no es encontrado
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
    * Obtiene una página de publicaciones que pertenecen a un subtipo específico.
    *
    * @param subtype El ID del subtipo de publicación
    * @param pageable La información de paginación
    * @return Una página de publicaciones que pertenecen al subtipo buscado
    * @throws Fail Si el subtipo no es encontrado
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
     * Obtiene publicaciones que se encuentran dentro del rango de fechas indicado.
     *
     * @param start Fecha de inicio del rango (inclusive)
     * @param end Fecha de fin del rango (inclusive)
     * @return Una página de publicaciones que se encuentran dentro del rango de fechas
     */
    @Override
    public Page<Post> findByDateRange(Timestamp start, Timestamp end, Pageable pageable) {
        return postRepository
                .findByDateRange(start, end, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene publicaciones que comienzan después de una fecha específica.
     *
     * @param start La fecha de inicio
     * @param pageable La información de paginación
     * @return Una página de publicaciones que comienzan después de la fecha especificada
     */
    @Override
    public Page<Post> findByDateStart(Timestamp start, Pageable pageable) {
        return postRepository
                .findByDateStart(start, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Obtiene publicaciones que comienzan antes de una fecha específica.
     *
     * @param end La fecha de fin
     * @param pageable La información de paginación
     * @return Una página de publicaciones que comienzan antes de la fecha especificada
     */
    @Override
    public Page<Post> findByDateEnd(Timestamp end, Pageable pageable) {
        return postRepository
                .findByDateEnd(end, pageable)
                .map(voteService::populate)
                .map(commentService::populate);
    }

    /**
     * Crea una nueva publicación.
     *
     * El usuario actual debe ser el dueño de la publicación.
     *
     * @param newPost La nueva publicación
     * @param originalPoster El usuario que que hace la petición
     * @return La publicación creada
     * @throws Fail Si:
     * -El subtipo no es encontrado
     * -El barrio no es encontrado
     */
    @Override
    public Post create(PostCreateRequest newPost, User originalPoster) {
        Post post = new Post();
        post.setOwner(originalPoster);
        post.setAudience((newPost.getAudience()));
        post.setTitle(newPost.getTitle());
        var subtype = postSubtypeRepository.findActiveById(newPost.getSubtype())
                .orElseThrow(() -> new Fail("Subtype not found. ", HttpStatus.BAD_REQUEST));
        post.setSubtype(subtype);
        post.setDescription(newPost.getDescription());
        var neighbourhood = neighbourhoodRepository
                .findActiveNeighbourhoodById(newPost.getNeighbourhood())
                .orElseThrow(() -> new Fail("Neighbourhood not found. ", HttpStatus.BAD_REQUEST));
        post.setNeighbourhood(neighbourhood);
        post.setTimestamp(newPost.getTimestamp());
        post.setTags(newPost.getTags());
        postRepository.save(post);
        return commentService.populate(voteService.populate(post));
    }

    /**
     * Actualiza una publicación.
     *
     * El usuario actual debe ser el dueño o un administrador
     * para poder actualizar la publicación.
     *
     * @param id El ID de la publicación
     * @param newPost La nueva publicación
     * @param originalPoster El usuario que realizó la actualización
     * @return La publicación actualizada
     * @throws AccessDeniedException Si:
     * -El usuario no es el dueño de la publicación
     * -El usuario no es administrador
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
            var subtype = postSubtypeRepository.findActiveById(newPost.getSubtype())
                    .orElseThrow(() -> new Fail("Subtype not found. ", HttpStatus.BAD_REQUEST));
            post.setSubtype(subtype);
        }
        if(newPost.getDescription() != null) post.setDescription(newPost.getDescription());
        if(newPost.getNeighbourhood() != null) {
            var neighbourhood = neighbourhoodRepository
                    .findActiveNeighbourhoodById(newPost.getNeighbourhood())
                    .orElseThrow(() -> new Fail("Neighbourhood not found. ", HttpStatus.BAD_REQUEST));
            post.setNeighbourhood(neighbourhood);
        }
        if(newPost.getTags() != null) post.setTags(newPost.getTags());
        postRepository.save(post);
        return commentService.populate(voteService.populate(post));
    }

    /**
     * Elimina una publicación.
     * Sólo el propietario de la publicación, el administrador o el moderador del barrio
     * de la publicación pueden eliminarla.
     *
     * @param id El ID de la publicación a eliminar
     * @param originalPoster El usuario que intenta eliminar la publicación
     * @throws AccessDeniedException Si el usuario no tiene permiso para eliminar la publicación
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
     * Ejecuta el SP "getPosts" y devuelve una lista de DTOs de publicaciones.
     *
     * @param userId El ID del usuario autenticado
     * @param userNeighbourhood El barrio del usuario autenticado
     * @return Una lista de DTOs de publicaciones
     */
    @Override
    public List<PostDTO> findPosts(int userId, int userNeighbourhood) {
        List<Map<String, Object>> rawResults = postRepository.findPosts(userId, userNeighbourhood);
        return rawResults.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Convierte una fila de resultados de la consulta a un DTO de publicación.
     *
     * @param row La fila de resultados
     * @return El DTO de la publicación
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
