package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import frgp.utn.edu.ar.quepasa.service.*;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

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

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest post) {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.create(post, me));
    }

    /**
     * <b>Endpoint TEMPORAL. </b>
     * <p>Se reemplazará por un algoritmo más eficaz, detallado en la Issue #98.</p>
     */
    @Deprecated
    @GetMapping("/all")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.listPost(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @GetMapping("/op/{id}")
    public ResponseEntity<?> getPostsByOp(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByOp(id, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getPostsByAuthUser(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        User me = authenticationService.getCurrentUserOrDie();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.findByOp(me.getId(), pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id, @RequestBody PostPatchEditRequest post) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.update(id, post, me));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        postService.delete(id, me);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    /** Comienza sección de VOTOS **/
    @GetMapping("/{id}/votes")
    public ResponseEntity<VoteCount> getVotes(@PathVariable Integer id) {
        return ResponseEntity.ok(voteService.count(Post.identify(id)));
    }

    @PostMapping("/{id}/votes/up")
    public ResponseEntity<VoteCount> upVote(@PathVariable Integer id) {
        var post = Post.identify(id);
        var voteResult = voteService.vote(post, 1);
        return ResponseEntity.ok(voteResult);
    }

    @PostMapping("/{id}/votes/down")
    public ResponseEntity<VoteCount> downVote(@PathVariable Integer id) {
        return ResponseEntity.ok(voteService.vote(Post.identify(id), -1));
    }
    /** Termina sección de VOTOS **/
    /**
     * Comienza sección de COMENTARIOS
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> comment(@PathVariable Integer id, @RequestBody String content) {
        return ResponseEntity.ok(commentService.create(content, Post.identify(id)));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<PostComment>> viewComments(@PathVariable Integer id, Pageable pageable) {
        return ResponseEntity.ok(commentService.findAllFromPost(id, pageable));
    }

    @ExceptionHandler(ValidatorBuilder.ValidationError.class)
    public ResponseEntity<ValidatorBuilder.ValidationError> handleValidationError(ValidatorBuilder.ValidationError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<NoSuchElementException> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex);
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<String> handleFail(Fail ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

}
