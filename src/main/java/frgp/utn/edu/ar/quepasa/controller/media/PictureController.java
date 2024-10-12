package frgp.utn.edu.ar.quepasa.controller.media;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.service.media.PictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    private PictureService pictureService;

    @Autowired @Lazy
    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @PostMapping
    public ResponseEntity<Picture> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description
    ) {
        Picture pic = pictureService.upload(file, description);
        return ResponseEntity.ok(pic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Picture>> getPicture(@PathVariable String id) {
        return ResponseEntity.ok(pictureService.getPictureById(id));
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewPicture(@PathVariable String id) {
        RawPicture res = pictureService.getRawPictureById(id);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "\"")
                .contentType((res.getPicture().getMediaType()))
                .body(res.getResource());
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
