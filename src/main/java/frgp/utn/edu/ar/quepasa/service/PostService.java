package frgp.utn.edu.ar.quepasa.service;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.response.PostDTO;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;

public interface PostService {
    Page<Post> search(String q, Pageable pageable, boolean active);
    Page<Post> findAll(Pageable pageable, boolean activeOnly);
    Post findById(Integer id);
    Page<Post> findByOp(Integer originalPoster, Pageable pageable);
    Page<Post> findByAudience(Audience audience, Pageable pageable);
    Page<Post> findByType(Integer type, Pageable pageable);
    Page<Post> findBySubtype(Integer subtype, Pageable pageable);
    Page<Post> findByDateRange(Timestamp start, Timestamp end, Pageable pageable);
    Page<Post> findByDateStart(Timestamp start, Pageable pageable);
    Page<Post> findByDateEnd(Timestamp end, Pageable pageable);
    Post create(PostCreateRequest newPost, User originalPoster);
    Post update(Integer id, PostPatchEditRequest newPost, User originalPoster) throws AccessDeniedException;
    void delete(Integer id, User originalPoster) throws AccessDeniedException;
    List<PostDTO> obtenerPosts(int userBarrio, int userId);
}
