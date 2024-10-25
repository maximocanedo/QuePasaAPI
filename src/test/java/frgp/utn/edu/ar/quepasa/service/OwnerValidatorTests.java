package frgp.utn.edu.ar.quepasa.service;


import frgp.utn.edu.ar.quepasa.exception.Fail;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.service.validators.OwnerValidatorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Tests de derechos sobre registros. ")
public class OwnerValidatorTests {

    private OwnableImpl ownable;
    private User currentUser;

    @BeforeEach
    public void setUp() {
        ownable = mock(OwnableImpl.class);
        var owner = new User();
        currentUser = new User();
        when(ownable.getOwner()).thenReturn(owner);
        owner.setUsername("ownerUsername");
    }

    @Test
    @WithMockUser(username = "ownerUsername", roles = { "ADMIN" })
    @DisplayName("El usuario es dueño y administrador. Se requieren ambos")
    public void ownerAndAdmin() {
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .and(
                        OwnerValidatorBuilder::isOwner,
                        OwnerValidatorBuilder::isAdmin
                );
        assert !builder.build();

    }

    @Test
    @WithMockUser(username = "ownerUsername", roles = { "GOVT" })
    @DisplayName("El usuario es dueño pero no administrador y se requieren ambos")
    public void ownerAndNotAdmin() {
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .and(
                        OwnerValidatorBuilder::isOwner,
                        OwnerValidatorBuilder::isAdmin
                );
        assert !builder.build();

    }

    @Test
    @WithMockUser(username = "anotherUser", roles = { "ADMIN" })
    @DisplayName("El usuario no es dueño aunque sí administrador y se requieren ambos")
    public void adminAndNotOwner() {
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .and(
                        OwnerValidatorBuilder::isOwner,
                        OwnerValidatorBuilder::isAdmin
                );
        assert !builder.build();
    }

    @Test
    @WithMockUser(username = "anotherUser", roles = { "USER" })
    @DisplayName("El usuario no es dueño ni administrador y se requieren ambos")
    public void notAdminAndNotOwner() {
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .and(
                        OwnerValidatorBuilder::isOwner,
                        OwnerValidatorBuilder::isAdmin
                );
        assert !builder.build();
    }

    @Test
    @DisplayName("Usuario actual es dueño del registro. ")
    @WithMockUser(username = "ownerUsername", roles = { "MOD" })
    public void isOwner() {
        currentUser.setRole(Role.MOD);
        currentUser.setUsername("ownerUsername");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner();
        assert builder.build();

        var builder2 = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner()
                .isAdmin();
        assert builder2.build();

    }

    @Test
    @WithMockUser(username = "root", roles = { "ADMIN" })
    @DisplayName("Usuario actual no es dueño del registro, pero sí administrador. ")
    public void isNotOwner() {
        currentUser.setRole(Role.ADMIN);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner();
        assert !builder.build();

        var builder2 = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner()
                .isAdmin();
        assert builder2.build();

        var builder3 = OwnerValidatorBuilder.create(ownable, currentUser)
                .and(
                    OwnerValidatorBuilder::isOwner,
                    OwnerValidatorBuilder::isAdmin
                );
        assert !builder3.build();

        var builder4 = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner()
                .isAdmin()
                .isModerator();
        assert builder4.build();

        var builder5 = OwnerValidatorBuilder.create(ownable, currentUser)
                .and(
                        OwnerValidatorBuilder::isOwner,
                        OwnerValidatorBuilder::isModerator
                );
        assert !builder5.build();
    }

    @Test
    @DisplayName("Usuario actual es administrador. ")
    public void isAdmin() {
        currentUser.setRole(Role.ADMIN);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isAdmin();
        assert builder.build();
    }

    @Test
    @DisplayName("Usuario actual es moderador. ")
    public void isModerator() {
        currentUser.setRole(Role.MOD);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isModerator();
        assert builder.build();
    }

    @Test
    @DisplayName("Usuario actual es entidad gubernamental. ")
    public void isGovernment() {
        currentUser.setRole(Role.GOVT);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isGovernment();
        assert builder.build();
    }

    @Test
    @DisplayName("Usuario actual es organización. ")
    public void isOrganization() {
        currentUser.setRole(Role.ORGANIZATION);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOrganization();
        assert builder.build();
    }

    @Test
    @DisplayName("Usuario actual es contribuidor. ")
    public void isContributor() {
        currentUser.setRole(Role.CONTRIBUTOR);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isContributor();
        assert builder.build();
    }

    @Test
    @DisplayName("Usuario actual es vecino. ")
    public void isNeighbour() {
        currentUser.setRole(Role.NEIGHBOUR);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isNeighbour();
        assert builder.build();
    }

    @Test
    @DisplayName("Usuario actual es usuario. ")
    public void isUser() {
        currentUser.setRole(Role.USER);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isUser();
        assert builder.build();
    }

    @Test
    @DisplayName("Comprobar que las excepciones se lancen adecuadamente. ")
    public void orElseFail() {

        currentUser.setRole(Role.USER);
        currentUser.setUsername("root");
        var builder = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner()
                .isAdmin();
        assertThrows(Fail.class, builder::orElseFail);

        currentUser.setRole(Role.ADMIN);
        var builder2 = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner()
                .isAdmin();
        assertDoesNotThrow(builder2::orElseFail);

        currentUser.setUsername("ownerUsername");
        var builder3 = OwnerValidatorBuilder.create(ownable, currentUser)
                .isOwner()
                .isAdmin();
        assertDoesNotThrow(builder3::orElseFail);
    }

}
