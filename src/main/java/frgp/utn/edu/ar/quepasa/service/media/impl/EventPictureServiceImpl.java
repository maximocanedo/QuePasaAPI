package frgp.utn.edu.ar.quepasa.service.media.impl;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Event;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.EventPicture;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.media.EventPictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.media.EventPictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import frgp.utn.edu.ar.quepasa.service.validators.objects.MultipartFileValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Service("eventPictureService")
public class EventPictureServiceImpl implements EventPictureService {

    private final EventPictureRepository pictureRepository;
    private final EventRepository eventRepository;
    private final StorageService storageService;
    private final AuthenticationService authenticationService;
    private final OwnerService ownerService;

    public EventPictureServiceImpl(EventPictureRepository pictureRepository, EventRepository eventRepository, StorageService storageService, AuthenticationService authenticationService, OwnerService ownerService) {
        this.pictureRepository = pictureRepository;
        this.eventRepository = eventRepository;
        this.storageService = storageService;
        this.authenticationService = authenticationService;
        this.ownerService = ownerService;
    }

    @Override
    public EventPicture upload(MultipartFile file, UUID eventId, String description) {
        User currentUser = authenticationService.getCurrentUserOrDie();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Fail("Event not found", HttpStatus.NOT_FOUND));
        EventPicture picture = new EventPicture();
        picture.setDescription(description);
        picture.setEvent(event);
        picture.setOwner(currentUser);
        var finalFile = new MultipartFileValidator(file)
                .isNotNull()
                .isNotEmpty()
                .hasContentType()
                .isPicture()
                .meetsMaximumSizeForPicture()
                .build();
        pictureRepository.save(picture);
        storageService.store(finalFile, "picture." + picture.getId().toString());
        picture.setActive(true);
        picture.setUploadedAt(new Timestamp(System.currentTimeMillis()));
        picture.setMediaType(MediaType.valueOf(Objects.requireNonNull(finalFile.getContentType())));
        pictureRepository.save(picture);
        return picture;
    }

    @Override
    public EventPicture getPictureById(UUID id) {
        return pictureRepository.findById(id)
                .orElseThrow(() -> new Fail("Picture not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public RawPicture getRawPictureById(UUID id) {
        var op = pictureRepository
                .findById(id);
        if (op.isEmpty() || !op.get().isActive()) throw new Fail("Picture not found. ", HttpStatus.NOT_FOUND);
        return new RawPicture(op.get(), storageService.loadAsResource("picture." + id, op.get().getMediaType()));
    }

    @Override
    public Page<EventPicture> getEventPics(UUID eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Fail("Event not found", HttpStatus.NOT_FOUND));
        return pictureRepository.findByEvent(event, pageable);
    }

    @Override
    public void delete(UUID id) {
        var doc = pictureRepository.findById(id);
        if (doc.isEmpty() || !doc.get().isActive()) {
            throw new Fail("Picture not found", HttpStatus.NOT_FOUND);
        }
        var file = doc.get();
        ownerService.of(file)
                .isOwner()
                .isAdmin();
        storageService.delete("picture." + file.getId().toString());
        pictureRepository.delete(doc.get());
    }
}
