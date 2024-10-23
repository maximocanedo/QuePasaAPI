package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.response.CommentCount;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.commenting.EventComment;
import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface CommentService {
    Comment findById(UUID id);
    CommentCount count(Post post);
    Post populate(Post commentable);
    CommentCount count(Event event);
    Event populate(Event commentable);
    Comment create(String content, Post post);
    Comment create(String content, Event event);
    Page<PostComment> findAllFromPost(Integer id, Pageable pageable);
    Page<EventComment> findAllFromEvent(UUID id, Pageable pageable);
    Comment update(UUID id, String content);
    void delete(UUID id) throws AccessDeniedException;

}
