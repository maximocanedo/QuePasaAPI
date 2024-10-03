package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.User;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface CommentService {
    Comment findById(UUID id);

    Comment create();

    Comment update(UUID id);

    void delete(UUID id, User author) throws AccessDeniedException;
}
