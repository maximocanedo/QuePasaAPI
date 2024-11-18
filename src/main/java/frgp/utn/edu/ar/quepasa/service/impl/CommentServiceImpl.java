package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.data.response.CommentCount;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.commenting.EventComment;
import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import frgp.utn.edu.ar.quepasa.repository.CommentRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.commenting.EventCommentRepository;
import frgp.utn.edu.ar.quepasa.repository.commenting.PostCommentRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import quepasa.api.validators.comments.CommentContentValidator;
import frgp.utn.edu.ar.quepasa.service.validators.objects.EventValidator;
import frgp.utn.edu.ar.quepasa.service.validators.objects.PostValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final EventCommentRepository eventCommentRepository;
    private final EventRepository eventRepository;
    private final AuthenticationService authenticationService;
    private final OwnerService ownerService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostCommentRepository postCommentRepository, PostRepository postRepository, EventCommentRepository eventCommentRepository, EventRepository eventRepository, AuthenticationService authenticationService, OwnerService ownerService) {
        this.commentRepository = commentRepository;
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
        this.eventCommentRepository = eventCommentRepository;
        this.eventRepository = eventRepository;
        this.authenticationService = authenticationService;
        this.ownerService = ownerService;
    }

    @Override
    public Comment findById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new Fail("Comment not found. ", HttpStatus.NOT_FOUND));
    }

    @Override
    public CommentCount count(Post post) {
        var cc = new CommentCount();
        cc.setCount(postCommentRepository.count(post.getId()));
        cc.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        return cc;
    }

    @Override
    public Post populate(Post commentable) {
        commentable.setCommentCount(count(commentable));
        return commentable;
    }

    @Override
    public CommentCount count(Event event) {
        var cc = new CommentCount();
        cc.setCount(eventCommentRepository.count(event.getId()));
        cc.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        return cc;
    }

    @Override
    public Event populate(Event commentable) {
        commentable.setCommentCount(count(commentable));
        return commentable;
    }

    @Override
    public Comment create(String content, Post file) {
        content = content.substring(1, content.length() - 1);
        var current = authenticationService.getCurrentUserOrDie();
        var f = postRepository.findById(file.getId());
        if(f.isEmpty() || !f.get().isActive()) throw new Fail("Post not found", HttpStatus.NOT_FOUND);
        Post post = new PostValidator(f.get())
                .canAccess(current).build();
        var comment = new PostComment();
        comment.setContent(
            new CommentContentValidator(content)
                .trim()
                .meetsLimits()
                .build()
        );
        comment.setAuthor(current);
        comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        comment.setPost(post);
        comment.setActive(true);
        return postCommentRepository.save(comment);
    }

    @Override
    public Comment create(String content, Event file) {
        content = content.substring(1, content.length() - 1);
        var current = authenticationService.getCurrentUserOrDie();
        var f = eventRepository.findById(file.getId());
        if(f.isEmpty() || !f.get().isActive()) throw new Fail("Event not found", HttpStatus.NOT_FOUND);
        Event post = new EventValidator(f.get())
                .canAccess(current).build();
        var comment = new EventComment();
        comment.setContent(
                new CommentContentValidator(content)
                        .trim()
                        .meetsLimits()
                        .build()
        );
        comment.setAuthor(current);
        comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        comment.setEvent(post);
        comment.setActive(true);
        return eventCommentRepository.save(comment);
    }

    @Override
    public Page<PostComment> findAllFromPost(Integer id, Pageable pageable) {
        var current = authenticationService.getCurrentUserOrDie();
        var f = postRepository.findById(id);
        if(f.isEmpty() || !f.get().isActive()) throw new Fail("Post not found", HttpStatus.NOT_FOUND);
        new PostValidator(f.get())
                .canAccess(current)
                .build();
        return postCommentRepository.list(id, pageable);
    }

    @Override
    public Page<EventComment> findAllFromEvent(UUID id, Pageable pageable) {
        var current = authenticationService.getCurrentUserOrDie();
        var f = eventRepository.findById(id);
        if(f.isEmpty() || !f.get().isActive()) throw new Fail("Event not found", HttpStatus.NOT_FOUND);
        new EventValidator(f.get())
                .canAccess(current)
                .build();
        return eventCommentRepository.list(id, pageable);
    }

    @Override
    public Comment update(UUID id, String content) {
        var commentOptional = commentRepository.findById(id);
        if(commentOptional.isEmpty() || !commentOptional.get().isActive())
            throw new Fail("Comment not found. ", HttpStatus.NOT_FOUND);
        var comment = commentOptional.get();
        ownerService.of(comment).isOwner();
        comment.setContent(
            new CommentContentValidator(content)
                .trim()
                .meetsLimits()
                .build()
        );
        return commentRepository.save(comment);
    }

    @Override
    public void delete(UUID id) {
        Comment comment = findById(id);
        ownerService.of(comment)
                .isOwner()
                .isAdmin()
                .orElseFail();
        comment.setActive(false);
        commentRepository.save(comment);
    }
}
