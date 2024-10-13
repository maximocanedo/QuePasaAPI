package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.Ownable;
import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.AuthenticationServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
@DisplayName("Servicio de propiedad de registros")
public class OwnerServiceTests {

    private final UserRepository userRepository;
    private final AuthenticationServiceImpl authenticationService;
    private final OwnerServiceImpl ownerService;

    User mockUser = new User();

    public OwnerServiceTests() {
        userRepository = Mockito.mock(UserRepository.class);
        authenticationService = Mockito.mock(AuthenticationServiceImpl.class);
        ownerService = new OwnerServiceImpl(authenticationService);
    }

    @BeforeEach
    void setUp() {
        var username = "albahaca";
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    }


    @Test
    @WithMockUser(username = "root", roles = { "ADMIN" })
    public void test() {
        User mockUser = new User();
        var username = "albahaca";
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        Ownable post = new Post();
        post.setOwner(mockUser);
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);

        assertDoesNotThrow(() -> {
            ownerService.of(post)
                    .isOwner()
                    .isAdmin()
                    .orElseFail();
        });
    }

}
