package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.service.PostTypeService;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post-types")
public class PostTypeController {

    private final PostTypeService postTypeService;
    private final AuthenticationService authenticationService;

    @Autowired
    public PostTypeController(PostTypeService postTypeService) {
        this.postTypeService = postTypeService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> createPostType(@RequestBody String description) {
        return ResponseEntity.ok(postTypeService.create(description));
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
    public ResponseEntity<?> updatePostType(@PathVariable Integer id, @RequestBody String description) {
        return ResponseEntity.ok(postTypeService.update(id, description));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostType(@PathVariable Integer id) {
        postTypeService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }



    @ExceptionHandler(ValidatorBuilder.ValidationError.class)
    public ResponseEntity<ValidatorBuilder.ValidationError> handleValidationError(ValidatorBuilder.ValidationError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }
}
