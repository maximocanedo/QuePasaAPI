package frgp.utn.edu.ar.quepasa.service.media.impl;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.media.PictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import frgp.utn.edu.ar.quepasa.service.validators.objects.MultipartFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    private PictureRepository pictureRepository;
    private StorageService storageService;
    private AuthenticationService authenticationService;
    private OwnerService ownerService;
    //private VoteService voteService;

    @Autowired
    public void setOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
    }
    /*
    @Autowired
    public void setVoteService(VoteService voteService) {
        this.voteService = voteService;
    }
    */
    @Autowired @Lazy
    public void setPictureRepository(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Autowired @Lazy
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired @Lazy
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Picture upload(MultipartFile file, String description) {
        User current = authenticationService.getCurrentUserOrDie();
        Picture picture = new Picture();
        picture.setDescription(description);
        picture.setOwner(current);
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
    public Page<Picture> getPictures(Pageable pageable) {
        return pictureRepository.findAllActive(pageable);
    }

    @Override
    public RawPicture getRawPictureById(UUID id) {
        var op = pictureRepository
                .findById(id);
        if(op.isEmpty() || !op.get().isActive()) throw new Fail("Picture not found. ", HttpStatus.NOT_FOUND);
        //return new RawPicture(voteService.populate(op.get()), storageService.loadAsResource("picture." + id.toString()));
        return new RawPicture(op.get(), storageService.loadAsResource("picture." + id, op.get().getMediaType()));
    }

    @Override
    public RawPicture getRawPictureById(String id) {
        return getRawPictureById(UUID.fromString(id));
    }

    @Override
    public Optional<Picture> getPictureById(UUID id) {
        return pictureRepository
                .findById(id);
    }

    @Override
    public Optional<Picture> getPictureById(String id) {
        return getPictureById(UUID.fromString(id));
    }

    @Override
    public Page<Picture> getMyPics(Pageable pageable) {
        var current = authenticationService.getCurrentUserOrDie();
        return pictureRepository
                .findByOwner(current, pageable);
    }

    @Override
    public void delete(UUID id) {
        var doc = pictureRepository.findById(id);
        if(doc.isEmpty() || !doc.get().isActive())
            throw new Fail("Picture not found. ", HttpStatus.NOT_FOUND);
        var file = doc.get();
        ownerService.of(file).isOwner().isAdmin();
        storageService.delete("picture."+file.getId().toString(), file.getMediaType());
        pictureRepository.delete(doc.get());
    }

}
