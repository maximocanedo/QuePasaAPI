package frgp.utn.edu.ar.quepasa.controller.commenting;

import frgp.utn.edu.ar.quepasa.data.request.event.EventCommentDTO;
import frgp.utn.edu.ar.quepasa.data.request.post.PostCommentDTO;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.commenting.EventComment;
import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/post")
    public ResponseEntity<Comment> createPostComment(@RequestBody PostCommentDTO file) {
        String content = file.getContent();
        Post post = file.getFile();
        return ResponseEntity.ok(commentService.create(content, post));
    }

    @PostMapping("/event")
    public ResponseEntity<Comment> createEventComment(@RequestBody EventCommentDTO file) {
        String content = file.getContent();
        Event event = file.getFile();
        return ResponseEntity.ok(commentService.create(content, event));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Page<PostComment>> getCommentsByPost(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.findAllFromPost(id, pageable));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Page<EventComment>> getCommentsByEvent(@PathVariable UUID id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.findAllFromEvent(id, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable UUID id, @RequestBody String content) {
        return ResponseEntity.ok(commentService.update(id, content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable UUID id) throws AccessDeniedException {
        commentService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
