package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.exception.Fail;
import quepasa.api.exceptions.ValidationError;
import frgp.utn.edu.ar.quepasa.fakedata.NapoleonBonaparteInspiredData;
import frgp.utn.edu.ar.quepasa.model.Comment;
import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.commenting.PostComment;
import frgp.utn.edu.ar.quepasa.repository.CommentRepository;
import frgp.utn.edu.ar.quepasa.repository.EventRepository;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.commenting.EventCommentRepository;
import frgp.utn.edu.ar.quepasa.repository.commenting.PostCommentRepository;
import frgp.utn.edu.ar.quepasa.service.impl.CommentServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
public class CommentServiceTests {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostCommentRepository postCommentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private EventCommentRepository eventCommentRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private OwnerService ownerService;

    private final NapoleonBonaparteInspiredData data = new NapoleonBonaparteInspiredData();

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#82: Comentar publicación")
    void testCreateCommentForPost_Success() {
        var post = data.post_A();
        var currentUser = data.napoleonBonaparte();

        when(postRepository.findById(data.post_A().getId())).thenReturn(Optional.of(post));
        when(authenticationService.getCurrentUserOrDie()).thenReturn(currentUser);
        when(postCommentRepository.save(any(PostComment.class))).thenAnswer(i -> i.getArguments()[0]);

        var comment = commentService.create("Test comment", post);

        assertNotNull(comment);
        assertEquals("Test comment", comment.getContent());
        verify(postCommentRepository).save(any(PostComment.class));
    }

    @Test
    @DisplayName("#82: Comentar publicación inexistente")
    void testCreateCommentForPost_NotFound() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> {
            commentService.create("Test comment", Post.identify(data.post_A().getId()));
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("#85: Editar comentario")
    void testUpdateComment_Success() {
        var comment = new PostComment();
        comment.setId(UUID.randomUUID());
        comment.setActive(true);

        var ob = mock(OwnerValidator.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));
        when(ownerService.of(any(Ownable.class))).thenReturn(ob);
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);

        var updatedComment = commentService.update(comment.getId(), "Updated content");

        assertEquals("Updated content", updatedComment.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("#85: Editar comentario no existente")
    void testUpdateComment_NotFound() {
        UUID id = UUID.randomUUID();
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> {
            commentService.update(id, "New content");
        });

        assertEquals("Comment not found. ", exception.getMessage());
    }

    @Test
    @DisplayName("#85: Editar comentario, excediendo el límite de caracteres. ")
    void testUpdateComment_ExcessCharacterLimit() {
        UUID id = UUID.randomUUID();
        var comment = new PostComment();
        comment.setId(UUID.randomUUID());
        comment.setActive(true);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));
        var ob = mock(OwnerValidator.class);
        when(ownerService.of(any(Ownable.class))).thenReturn(ob);

        String content = "xyz".repeat(100);

        var exception = assertThrows(ValidationError.class, () -> {
            commentService.update(id, content);
        });

        assertEquals("content", exception.getField());
    }

    @Test
    @DisplayName("#82: Listar comentarios de una publicación")
    void testFindAllFromPost_Success() {
        var post = data.post_A();
        var pageable = Pageable.ofSize(10);
        Page<PostComment> page = mock(Page.class);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(authenticationService.getCurrentUserOrDie()).thenReturn(data.napoleonBonaparte());
        when(postCommentRepository.list(1, pageable)).thenReturn(page);

        var comments = commentService.findAllFromPost(1, pageable);

        assertNotNull(comments);
        verify(postCommentRepository).list(1, pageable);
    }

    @Test
    @DisplayName("#82: Listar comentarios de una publicación que no existe")
    void testFindAllFromPost_NotFound() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        Fail exception = assertThrows(Fail.class, () -> {
            commentService.findAllFromPost(1, Pageable.ofSize(10));
        });

        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("#86: Borrar comentario")
    void testDeleteComment_Success() {
        UUID id = UUID.randomUUID();
        var comment = new PostComment();
        comment.setId(id);
        comment.setActive(true);

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        var ob = mock(OwnerValidator.class);
        when(ownerService.of(any(Ownable.class))).thenReturn(ob);
        when(ob.isOwner()).thenReturn(ob);
        when(ob.isAdmin()).thenReturn(ob);
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);

        commentService.delete(id);

        verify(commentRepository).save(comment);
        assertFalse(comment.isActive());
    }

    @Test
    @DisplayName("#86: Borrar comentario inexistente")
    void testDeleteComment_NotFound() {
        UUID id = UUID.randomUUID();
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(Fail.class, () -> {
            commentService.delete(id);
        });

        assertEquals("Comment not found. ", exception.getMessage());
    }

}
