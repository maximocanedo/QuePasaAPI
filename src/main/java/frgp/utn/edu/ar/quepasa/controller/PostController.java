package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        postService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
