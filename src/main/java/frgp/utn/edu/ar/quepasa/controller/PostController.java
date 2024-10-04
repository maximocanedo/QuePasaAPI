package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.request.post.PostCreateRequest;
import frgp.utn.edu.ar.quepasa.data.request.post.PostPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateRequest post) {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postService.create(post, me));
    }

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

}
