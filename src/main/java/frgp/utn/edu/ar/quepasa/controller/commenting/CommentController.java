package frgp.utn.edu.ar.quepasa.controller.commenting;

import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final VoteService voteService;

    @Autowired
    public CommentController(CommentService commentService, VoteService voteService) {
        this.commentService = commentService;
        this.voteService = voteService;
    }

    @PostMapping("/{commentId}/votes/up")
    public ResponseEntity<VoteCount> upVoteComment(@PathVariable UUID commentId) {
        Comment comment = commentService.findById(commentId);
        return ResponseEntity.ok(voteService.vote(comment, 1));
    }

    @PostMapping("/{commentId}/votes/down")
    public ResponseEntity<VoteCount> downVoteComment(@PathVariable UUID commentId) {
        Comment comment = commentService.findById(commentId);
        return ResponseEntity.ok(voteService.vote(comment, -1));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable UUID commentId, @RequestBody String content) {
        return ResponseEntity.ok(commentService.update(commentId, content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable UUID id) throws AccessDeniedException {
        commentService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}
