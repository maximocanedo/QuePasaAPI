package frgp.utn.edu.ar.quepasa.service.media;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.service.validators.objects.MultipartFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String finalFilename) {
        try {
            var finalFile = new MultipartFileValidator(file)
                    .isNotNull()
                    .isNotEmpty()
                    .hasContentType()
                    .build();

            String originalFilename = finalFile.getOriginalFilename();
            String fileExtension = null;

            if(originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            if(fileExtension == null) {
                fileExtension = ".jpg";
            }

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(finalFilename))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = finalFile.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename, MediaType mediaType) {
        try {
            Path file = load(filename);
            String fileExtension = "." + mediaType.getSubtype();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new Fail("File not found: " + filename, HttpStatus.NOT_FOUND);
            }
        }
        catch (MalformedURLException e) {
            throw new Fail("Could not read file: " + filename, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void delete(String filename, MediaType mediaType) {
        try {

            Path path = load(filename);
            String fileExtension = "." + mediaType.getSubtype();
            path = Paths.get(path.toString() + fileExtension);
            Files.delete(path);
        } catch (MalformedURLException e) {
            throw new Fail("Could not read file: " + filename, HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new Fail("Could not completely remove your file. ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}