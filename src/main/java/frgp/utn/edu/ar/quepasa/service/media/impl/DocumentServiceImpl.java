package frgp.utn.edu.ar.quepasa.service.media.impl;

import frgp.utn.edu.ar.quepasa.data.response.RawDocument;
import frgp.utn.edu.ar.quepasa.data.response.RawPicture;
import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.media.Document;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.media.DocumentRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.OwnerService;
import frgp.utn.edu.ar.quepasa.service.media.DocumentService;
import frgp.utn.edu.ar.quepasa.service.media.PictureService;
import frgp.utn.edu.ar.quepasa.service.media.StorageService;
import frgp.utn.edu.ar.quepasa.service.validators.MultipartFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentRepository documentRepository;
    private StorageService storageService;
    private AuthenticationService authenticationService;
    private OwnerService ownerService;

    @Autowired @Lazy
    public void setPictureRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Autowired
    public void setOwnerService(OwnerService ownerService) {
        this.ownerService = ownerService;
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
    public Document upload(MultipartFile file, String description) {
        User current = authenticationService.getCurrentUserOrDie();
        Document document = new Document();
        document.setDescription(description);
        document.setOwner(current);
        var finalFile = new MultipartFileValidator(file)
                .isNotNull()
                .isNotEmpty()
                .hasContentType()
                .isPDF()
                .meetsMaximumSizeForPDF()
                .build();
        documentRepository.save(document);
        storageService.store(finalFile, "document." + document.getId().toString());
        document.setActive(true);
        document.setUploadedAt(new Timestamp(System.currentTimeMillis()));
        documentRepository.save(document);
        return document;
    }

    @Override
    public RawDocument getRawDocumentById(UUID id) {
        var op = documentRepository.findById(id);
        if(op.isEmpty() || !op.get().isActive()) throw new Fail("Document not found. ", HttpStatus.NOT_FOUND);
        ownerService.of(op.get()).isOwner().isAdmin();
        return new RawDocument(op.get(), storageService.loadAsResource("document." + id.toString()));
    }

    @Override
    public RawDocument getRawDocumentById(String id) {
        return getRawDocumentById(UUID.fromString(id));
    }

    @Override
    public Optional<Document> getDocumentById(UUID id) {
        var doc = documentRepository.findById(id);
        if(doc.isEmpty() || !doc.get().isActive())
            throw new Fail("Document not found. ", HttpStatus.NOT_FOUND);
        ownerService.of(doc.get()).isOwner().isAdmin();
        return doc;
    }

    @Override
    public Optional<Document> getDocumentById(String id) {
        return getDocumentById(UUID.fromString(id));
    }

    @Override
    public Page<Document> getMyDocuments(Pageable pageable) {
        var current = authenticationService.getCurrentUserOrDie();
        return documentRepository.findByOwner(current, pageable);
    }

    @Override
    public void delete(UUID id) {
        var doc = documentRepository.findById(id);
        if(doc.isEmpty() || !doc.get().isActive())
            throw new Fail("Document not found. ", HttpStatus.NOT_FOUND);
        var file = doc.get();
        ownerService.of(file).isOwner().isAdmin();
        storageService.delete("document."+file.getId().toString());
        documentRepository.delete(doc.get());
    }

}
