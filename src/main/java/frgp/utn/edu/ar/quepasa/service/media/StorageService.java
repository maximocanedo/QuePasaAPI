package frgp.utn.edu.ar.quepasa.service.media;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void delete(String filename, MediaType mediaType);

    void init();

    void store(MultipartFile file, String finalFilename);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename, MediaType mediaType);

    void deleteAll();

}