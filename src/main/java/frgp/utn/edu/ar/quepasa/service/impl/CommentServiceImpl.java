package frgp.utn.edu.ar.quepasa.service.impl;

import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.repository.CommentRepository;
import frgp.utn.edu.ar.quepasa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment findById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public Comment create() {
        // TODO: Not implemented yet
        return null;
    }

    public Comment update(UUID id) {
        // TODO: Not implemented yet
        return null;
    }

    public void delete(UUID id, User author) throws AccessDeniedException {
        Comment comment = findById(id);
        if(!comment.getAuthor().getUsername().equals(author.getUsername())
                && !author.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Insufficient permissions");
        }
        comment.setActive(false);
        commentRepository.save(comment);
    }
}
