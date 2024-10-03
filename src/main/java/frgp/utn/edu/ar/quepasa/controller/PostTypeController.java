package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostTypeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/post-types")
public class PostTypeController {

    private final PostTypeService postTypeService;
    private final AuthenticationService authenticationService;

    PostTypeController(PostTypeService postTypeService, AuthenticationService authenticationService) {
        this.postTypeService = postTypeService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> createPostType(@RequestBody String description) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postTypeService.create(description, me));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getPostTypes(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postTypeService.listPostTypes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(postTypeService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePostType(@PathVariable Integer id, @RequestBody String description) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postTypeService.update(id, description, me));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostType(@PathVariable Integer id) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        postTypeService.delete(id, me);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
