package frgp.utn.edu.ar.quepasa.controller.users;

import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.data.request.SigninRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@DisplayName("Controladores de usuarios")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTests {

    private MockMvc mockMvc;
    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    @Autowired @Lazy
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Autowired @Lazy
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired @Lazy
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private String token = "";

    @BeforeAll
    public void setup() {
        var search = userRepository.findByUsername("mockUser0001");
        if(search.isEmpty()) {
            var req = new SignUpRequest();
            req.setUsername("mockUser0001");
            req.setPassword("P455w0&d+");
            req.setName("Usuario de prueba para inicio de sesión. ");
            req.setNeighbourhoodId(1);
            this.token = authenticationService.signup(req).getToken();
        } else {
            User e = search.get();
            e.setTotp("no-totp");
            userRepository.save(e);
            var req = new SigninRequest();
            req.setUsername("mockUser0001");
            req.setPassword("M4x%$4gv4nt320rb4");
            this.token = authenticationService.login(req).getToken();
        }
    }

    @Test
    @DisplayName("Búsqueda de usuarios")
    //@WithMockUser(username = "mockUser0001", roles = { "NEIGHBOUR" })
    public void userSearch() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + token)
                .param("q", "")
                .param("page", "0")
                .param("size", "10")
                .contentType("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    @DisplayName("Búsqueda de usuarios, sin autenticar")
    public void userSearchNoAuth() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("q", "")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType("application/json")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.first").doesNotExist());
    }


    @Test
    @DisplayName("Búsqueda de usuarios: Buscar usuario existente, con rol Usuario")
    @WithMockUser(username = "mockTest", roles = { "USER" })
    public void userFindByUsername__UserFound_WithUserRole() throws Exception {
        mockMvc.perform(get("/api/users/root")
                        .contentType("application/json")
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Búsqueda de usuarios: Buscar usuario existente, con rol Vecino")
    //@WithMockUser(username = "mockTest", roles = { "NEIGHBOUR", "USER" })
    public void userFindByUsername__UserFound_WithNeighbourRole() throws Exception {
        mockMvc.perform(get("/api/users/root")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.username").value("root"));
    }

}
