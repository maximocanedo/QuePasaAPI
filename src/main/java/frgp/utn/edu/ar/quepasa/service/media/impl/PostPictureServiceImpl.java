package frgp.utn.edu.ar.quepasa.service.media.impl;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.PostPicture;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PostPictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.media.PostPictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import frgp.utn.edu.ar.quepasa.service.validators.objects.MultipartFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class PostPictureServiceImpl implements PostPictureService {

    private PostPictureRepository pictureRepository;
    private PostRepository postRepository;
    private StorageService storageService;
    private AuthenticationService authenticationService;
    private OwnerService ownerService;

    @Autowired @Lazy
    public void setPictureRepository(PostPictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Autowired @Lazy
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired @Lazy
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired @Lazy
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Override
    public PostPicture upload(MultipartFile file, Integer post, String description) {
        User current = authenticationService.getCurrentUserOrDie();
        Post ownerPost = postRepository.findById(post)
                .orElseThrow(() -> new Fail("Post not found", HttpStatus.NOT_FOUND));
        PostPicture picture = new PostPicture();
        picture.setDescription(description);
        picture.setOwner(current);
        picture.setPost(ownerPost);
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
        pictureRepository.save(picture);

        return picture;
    }

    @Override
    public PostPicture getPictureById(UUID id) {
        return pictureRepository.findById(id)
                .orElseThrow(() -> new Fail("Picture not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Page<PostPicture> getPostPics(Integer id, Pageable pageable) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new Fail("Post not found", HttpStatus.NOT_FOUND));
        return pictureRepository.findByPost(post, pageable);
    }

    @Override
    public void delete(UUID id) {
        var doc = pictureRepository.findById(id);
        if(doc.isEmpty() || !doc.get().isActive())
            throw new Fail("Picture not found. ", HttpStatus.NOT_FOUND);
        var file = doc.get();
        ownerService.of(file).isOwner().isAdmin();
        storageService.delete("picture."+file.getId().toString());
        pictureRepository.delete(doc.get());
    }
}
