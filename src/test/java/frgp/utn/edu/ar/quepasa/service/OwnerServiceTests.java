package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class OwnerServiceTests {

    @MockBean
    private UserRepository userRepository;
    @Mock private AuthenticationServiceImpl authenticationService;
    @InjectMocks
    private OwnerServiceImpl ownerService;

    User mockUser = new User();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var username = "albahaca";
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    }


    @Test
    @WithMockUser(username = "root")
    public void test() {
        Ownable post = new Post();
        post.setOwner(mockUser);
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(mockUser));

        assertDoesNotThrow(() -> {
            ownerService.of(post)
                    .isOwner()
                    .orElseFail();
        });
    }

}
