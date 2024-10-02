package frgp.utn.edu.ar.quepasa.records;

import frgp.utn.edu.ar.quepasa.data.request.SignUpRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.service.AuthenticationService;
import frgp.utn.edu.ar.quepasa.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserRecords {

    private static UserService userService;
    private static AuthenticationService authenticationService;

    @Autowired
    public UserRecords(UserService userService, AuthenticationService authenticationService) {
        UserRecords.userService = userService;
        UserRecords.authenticationService = authenticationService;
    }

    public static User ANTONIO_GONZALEZ;

    private static void antonioGonzalez() {
        SignUpRequest req = new SignUpRequest();
        req.setUsername("antonio.gonzalez.ok");
        req.setPassword("antonio.gonzalez.ok");
        req.setName("Antonio González");
        req.setNeighbourhoodId(1);
        authenticationService.signup(req);
        ANTONIO_GONZALEZ = userService.findByUsername(req.getUsername());
    }

    @BeforeAll
    public static void setup() {
        antonioGonzalez();
        assertNotNull(ANTONIO_GONZALEZ, "No se creó el usuario de prueba Antonio González. ");
    }

}
