package frgp.utn.edu.ar.quepasa.controller.media;

import frgp.utn.edu.ar.quepasa.model.media.PostPicture;
import frgp.utn.edu.ar.quepasa.service.media.PostPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/post-pictures")
public class PostPictureController {

    private PostPictureService pictureService;

    @Autowired @Lazy
    public void setPictureService(PostPictureService pictureService) {
        this.pictureService = pictureService;
    }

    @PostMapping
    public ResponseEntity<PostPicture> upload(@RequestParam("file") MultipartFile file, @RequestParam("description") String description) {
        PostPicture pic = pictureService.upload(file, description);
        return ResponseEntity.ok(pic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostPicture> getPictureById(@PathVariable UUID id) {
        return ResponseEntity.ok(pictureService.getPictureById(id));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Page<PostPicture>> getPicturesByPost(@PathVariable Integer id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(pictureService.getPostPics(id, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        pictureService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
