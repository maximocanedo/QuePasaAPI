package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post-subtypes")
public class PostSubtypeController {

    private final PostSubtypeService postSubtypeService;

    private final AuthenticationService authenticationService;

    @Autowired
    public PostSubtypeController(PostSubtypeService postSubtypeService, AuthenticationService authenticationService) {
        this.postSubtypeService = postSubtypeService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> createPostSubtype(@RequestBody PostSubtypeRequest subtype) {
        return ResponseEntity.ok(postSubtypeService.create(subtype));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getPostSubtypes(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postSubtypeService.listPostSubtypes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostSubtypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(postSubtypeService.findById(id));
    }

    @GetMapping("/type/{id}")
    public ResponseEntity<?> getPostSubtypesByType(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postSubtypeService.findByType(id, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePostSubtype(@PathVariable Integer id, @RequestBody PostSubtypeRequest subtype) {
        return ResponseEntity.ok(postSubtypeService.update(id, subtype));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostSubtype(@PathVariable Integer id)  {
        postSubtypeService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ValidatorBuilder.ValidationError.class)
    public ResponseEntity<ValidatorBuilder.ValidationError> handleValidationError(ValidatorBuilder.ValidationError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<String> handleFail(Fail ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

}
