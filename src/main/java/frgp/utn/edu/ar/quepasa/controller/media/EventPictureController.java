package frgp.utn.edu.ar.quepasa.controller.media;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.model.media.EventPicture;
import frgp.utn.edu.ar.quepasa.service.media.EventPictureService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/event-pictures")
public class EventPictureController {
    private final EventPictureService eventPictureService;

    public EventPictureController(EventPictureService pictureService) {
        this.eventPictureService = pictureService;
    }

    @PostMapping
    public ResponseEntity<EventPicture> upload(@RequestParam("file") MultipartFile file, @RequestParam("eventId") UUID eventId, @RequestParam("description") String description) {
        EventPicture pic = eventPictureService.upload(file, eventId, description);
        return ResponseEntity.ok(pic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventPicture> getPictureById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventPictureService.getPictureById(id));
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewPicture(@PathVariable UUID id) {
        RawPicture res = eventPictureService.getRawPictureById(id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "\"")
                .contentType((res.getPicture().getMediaType()))
                .body(res.getResource());
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<Page<EventPicture>> getEventPics(@PathVariable UUID id, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventPictureService.getEventPics(id, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        eventPictureService.delete(id);
        return ResponseEntity.status(204).build();
    }
}
