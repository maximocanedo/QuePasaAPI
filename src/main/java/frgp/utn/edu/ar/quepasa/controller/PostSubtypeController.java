package frgp.utn.edu.ar.quepasa.controller;

import frgp.utn.edu.ar.quepasa.data.request.post.subtype.PostSubtypeRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.PostSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/post-subtypes")
public class PostSubtypeController {
    @Autowired
    private PostSubtypeService postSubtypeService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> createPostSubtype(@RequestBody PostSubtypeRequest subtype) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postSubtypeService.create(subtype, me));
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
    public ResponseEntity<?> updatePostSubtype(@PathVariable Integer id, @RequestBody PostSubtypeRequest subtype) throws AccessDeniedException {
        User me = authenticationService.getCurrentUserOrDie();
        return ResponseEntity.ok(postSubtypeService.update(id, subtype, me));
    }
}
