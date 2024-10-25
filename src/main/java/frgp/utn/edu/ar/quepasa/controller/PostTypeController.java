package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/post-types")
public class PostTypeController {

    private final PostTypeService postTypeService;
    private final AuthenticationService authenticationService;

    @Autowired
    public PostTypeController(PostTypeService postTypeService, AuthenticationService authenticationService) {
        this.postTypeService = postTypeService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> createPostType(@RequestBody String description) {
        return ResponseEntity.ok(postTypeService.create(description));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getPostTypes(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size,  @RequestParam(defaultValue="true") boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postTypeService.findAll(pageable, activeOnly));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getPostTypes(@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="description,asc") String sort, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size, @RequestParam(defaultValue="true") boolean active) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(sort.contains("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(postTypeService.search(q, pageable, active));
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

    @ExceptionHandler(ValidationError.class)
    public ResponseEntity<ValidationError> handleValidationError(ValidationError e) {
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
