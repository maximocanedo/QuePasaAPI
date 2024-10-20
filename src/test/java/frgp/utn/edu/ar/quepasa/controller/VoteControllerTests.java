package frgp.utn.edu.ar.quepasa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import frgp.utn.edu.ar.quepasa.data.response.VoteCount;
import frgp.utn.edu.ar.quepasa.fakedata.NapoleonBonaparteInspiredData;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.voting.PostVote;
import frgp.utn.edu.ar.quepasa.repository.votes.CommentVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.EventVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.PictureVoteRepository;
import frgp.utn.edu.ar.quepasa.repository.votes.PostVoteRepository;
import frgp.utn.edu.ar.quepasa.service.VoteService;
import frgp.utn.edu.ar.quepasa.service.geo.impl.SubnationalDivisionServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.impl.VoteServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest()
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controlador de votos")
public class VoteControllerTests {

    @Autowired private MockMvc mockMvc;
    @MockBean private VoteService voteService;
    @MockBean private PostVoteRepository postVoteRepository;
    @MockBean private AuthenticationServiceImpl authenticationService;
    private final NapoleonBonaparteInspiredData fake = new NapoleonBonaparteInspiredData();
    @Autowired private ObjectMapper objectMapper;

    public VoteControllerTests() {
        MockitoAnnotations.openMocks(this);
       /* this.postVoteRepository = Mockito.mock(PostVoteRepository.class);
        this.authenticationService = Mockito.mock(AuthenticationServiceImpl.class);
        this.voteService = new VoteServiceImpl(
                Mockito.mock(CommentVoteRepository.class),
                Mockito.mock(PictureVoteRepository.class),
                postVoteRepository,
                Mockito.mock(EventVoteRepository.class),
                authenticationService
        ); */
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
        when(postVoteRepository.getVotes(fake.post_A().getId())).thenReturn(0);
        when(postVoteRepository.getUserVote(fake.post_A().getId(), alpha().getUsername())).thenReturn(Optional.empty());
        when(postVoteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Votar una publicación")
    @WithMockUser(username = "root", roles = { "NEIGHBOUR" })
    public void vote() throws Exception {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(alpha()));
        when(authenticationService.getCurrentUserOrDie()).thenReturn(alpha());
        var c = new VoteCount();
        c.setUservote(1);
        c.setVotes(1);
        when(voteService.vote(ArgumentMatchers.any(Post.class),ArgumentMatchers.anyInt())).thenReturn(c);
        mockMvc.perform(
            post("/api/posts/" + fake.post_A().getId() + "/votes/up")
                .with(user("root").password("123456789").roles("ADMIN"))
        )
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.votes").exists())
                .andExpect(jsonPath("$.votes").value(1))
            .andExpect(jsonPath("$.uservote").exists());

    }


}
