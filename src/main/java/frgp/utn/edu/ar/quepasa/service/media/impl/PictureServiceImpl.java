package frgp.utn.edu.ar.quepasa.service.media.impl;

import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.media.PictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    private PictureRepository pictureRepository;
    private StorageService storageService;
    private AuthenticationService authenticationService;

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
        if(file.getContentType() != null)
            picture.setMediaType(MediaType.parseMediaType(
                    !file.getContentType().startsWith("image/")
                    ? "image/jpeg" : file.getContentType()
            ));
        pictureRepository.save(picture);
        storageService.store(file, "picture." + picture.getId().toString());
        picture.setActive(true);
        picture.setUploadedAt(new Timestamp(System.currentTimeMillis()));
        pictureRepository.save(picture);

        return picture;
    }

    @Override
    public RawPicture getRawPictureById(UUID id) {
        Picture op = pictureRepository
                .findById(id)
                .orElseThrow(NoSuchElementException::new);
        if(!op.isActive()) throw new NoSuchElementException();
        return new RawPicture(op, storageService.loadAsResource("picture." + id.toString()));
    }

    @Override
    public RawPicture getRawPictureById(String id) {
        return getRawPictureById(UUID.fromString(id));
    }

    @Override
    public Optional<Picture> getPictureById(UUID id) {
        return pictureRepository.findById(id);
    }

    @Override
    public Optional<Picture> getPictureById(String id) {
        return getPictureById(UUID.fromString(id));
    }

}
