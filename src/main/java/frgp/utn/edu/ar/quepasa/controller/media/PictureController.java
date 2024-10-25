package frgp.utn.edu.ar.quepasa.controller.media;

import frgp.utn.edu.ar.quepasa.data.ResponseError;
import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.exception.ValidationError;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import frgp.utn.edu.ar.quepasa.service.media.PictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    private PictureService pictureService;
    private VoteService voteService;

    @Autowired @Lazy
    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @Autowired @Lazy
    public void setVoteService(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<Picture> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description
    ) {
        Picture pic = pictureService.upload(file, description);
        return ResponseEntity.ok(pic);
    }

    @GetMapping
    public ResponseEntity<Page<Picture>> getMyPics(Pageable pageable) {
        var page = pictureService.getMyPics(pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        pictureService.delete(id);
        return ResponseEntity.status(204).build();
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

    /** Comienza sección de VOTOS **/
    @GetMapping("/{id}/votes")
    public ResponseEntity<VoteCount> getVotes(@PathVariable UUID id) {
        return ResponseEntity.ok(voteService.count(Picture.identify(id)));
    }

    @PostMapping("/{id}/votes/up")
    public ResponseEntity<VoteCount> upVote(@PathVariable UUID id) {
        var post = Picture.identify(id);
        var voteResult = voteService.vote(post, 1);
        return ResponseEntity.ok(voteResult);
    }

    @PostMapping("/{id}/votes/down")
    public ResponseEntity<VoteCount> downVote(@PathVariable UUID id) {
        return ResponseEntity.ok(voteService.vote(Picture.identify(id), -1));
    }
    /** Termina sección de VOTOS **/


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Fail.class)
    public ResponseEntity<ResponseError> handleFail(Fail e) {
        return ResponseEntity.status(e.getStatus()).body(new ResponseError(e.getMessage()));
    }

    @ExceptionHandler(ValidationError.class)
    public ResponseEntity<ValidationError> handleValidationError(ValidationError e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

}
