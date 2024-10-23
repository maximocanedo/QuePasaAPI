package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.fakedata.NapoleonBonaparteInspiredData;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.voting.PostVote;
import frgp.utn.edu.ar.quepasa.repository.votes.CommentVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.EventVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.PictureVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.PostVoteRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.VoteServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
@DisplayName("Servicio de votos")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VoteServiceTests {

    private final VoteService voteService;
    private final PostVoteRepository postVoteRepository;
    private final EventVoteRepository eventVoteRepository;
    private final AuthenticationServiceImpl authenticationService;
    private final NapoleonBonaparteInspiredData fake = new NapoleonBonaparteInspiredData();

    public VoteServiceTests() {
        this.postVoteRepository = Mockito.mock(PostVoteRepository.class);
        this.eventVoteRepository = Mockito.mock(EventVoteRepository.class);
        this.authenticationService = Mockito.mock(AuthenticationServiceImpl.class);
        this.voteService = new VoteServiceImpl(
                Mockito.mock(CommentVoteRepository.class),
                Mockito.mock(PictureVoteRepository.class),
                postVoteRepository,
                Mockito.mock(EventVoteRepository.class),
                authenticationService
        );
    }

    public User alpha() {
        var user = new User();
        user.setId(388);
        user.setUsername("alpha");
        user.setPassword("alpha");
        user.setRole(Role.NEIGHBOUR);
        user.setName("Usuario de prueba ALPHA");
        user.setAddress("");
        user.setNeighbourhood(fake.longwood());
        return user;
    }

    public PostVote alphaPostVote() {
        var pv = new PostVote();
        pv.setPost(fake.post_A());
        pv.setVote(1);
        pv.setId(11001);
        pv.setTimestamp(new Timestamp(System.currentTimeMillis()));
        pv.setVoter(alpha());
        return pv;
    }

    @BeforeAll
    public void setup() {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(alpha()));
        when(authenticationService.getCurrentUserOrDie()).thenReturn(alpha());
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(0);
        when(postVoteRepository.getUserVote(fake.post_A().getId(), alpha().getUsername())).thenReturn(Optional.empty());
        when(postVoteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Votar una publicación")
    @WithMockUser(username = "alpha", roles = { "NEIGHBOUR" })
    public void votePost() {
        var count = voteService.vote(fake.post_A(), 1);
        assertNotNull(count);
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(1);
        count = voteService.count(fake.post_A());
        assertNotNull(count);
        assertEquals(1, count.getVotes());

    }


    @Test
    @DisplayName("Votar un valor inválido a una publicación")
    public void voteNonExistentPostVote() {
        var count = voteService.vote(fake.post_A(), 1596);
        assertNotNull(count);
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(1);
        count = voteService.count(fake.post_A());
        assertNotNull(count);
        assertEquals(1, count.getVotes());

    }

    @Test
    @DisplayName("Votar una publicación ya votada")
    public void voteExistingPostVote() {
        when(postVoteRepository.getUserVote(fake.post_A().getId(), alpha().getUsername())).thenReturn(Optional.of(alphaPostVote()));
        var count = voteService.vote(fake.post_A(), 1);
        assertNotNull(count);
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(0);
        count = voteService.count(fake.post_A());
        assertNotNull(count);
        assertEquals(0, count.getVotes());
    }

    @Test
    @DisplayName("Contar votos de una publicación")
    public void countPostVotes() {
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(2);
        var count = voteService.count(fake.post_A());
        assertNotNull(count);
        assertEquals(2, count.getVotes());
    }

    @Test
    @DisplayName("Contar votos de una publicación inexistente")
    public void countNonExistentPostVotes() {
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(null);
        var count = voteService.count(fake.post_A());
        assertNotNull(count);
        assertEquals(0, count.getVotes());
    }

    @Test
    @DisplayName("Contar votos de una publicación con votos negativos")
    public void countNegativePostVotes() {
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(-2);
        var count = voteService.count(fake.post_A());
        assertNotNull(count);
        assertEquals(-2, count.getVotes());
    }

    /*
    @Test
    @DisplayName("Votar un evento")
    public void voteEvent() {
        var count = voteService.vote(fake.event_A(), 1);
        assertNotNull(count);
        when(eventVoteRepository.getVotes(fake.event_A().getId())).thenReturn(1);
        count = voteService.count(fake.event_A());
        assertNotNull(count);
        assertEquals(1, count.getVotes());
    }
     */
}
