package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.data.request.user.UserPatchEditRequest;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.media.Picture;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.media.PictureRepository;
import frgp.utn.edu.ar.quepasa.service.impl.UserServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTests {

    @Mock private UserRepository userRepository;
    @Mock private NeighbourhoodRepository neighbourhoodRepository;
    @Mock private PictureRepository pictureRepository;
    @InjectMocks private UserServiceImpl userService;
    @Mock private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Buscar usuario por nombre de usuario. ")
    void findByUsername_UserFound_ReturnsUser() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        User foundUser = userService.findByUsername(username);
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    @DisplayName("Modificar usuario por nombre de usuario. ")
    void updateUser_UserFound_GoodData() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        var request = new UserPatchEditRequest();
        request.setName("Arroz con leche");
        request.setAddress("Balcarce 50");
        AtomicReference<User> foundUser = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            foundUser.set(userService.update("testUser", request));
        });
        assertNotNull(foundUser.get());
        assertEquals(username, foundUser.get().getUsername());
        assertEquals("Arroz con leche", foundUser.get().getName());
        assertEquals("Balcarce 50", foundUser.get().getAddress());
    }

    @Test
    @DisplayName("Modificar usuario autenticado. ")
    @WithMockUser(username = "testUser")
    void updateUser_CurrentUser_GoodData() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        var request = new UserPatchEditRequest();
        request.setName("Arroz con leche");
        request.setAddress("Balcarce 50");
        AtomicReference<User> foundUser = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            foundUser.set(userService.update(request));
        });
        assertNotNull(foundUser.get());
        assertEquals(username, foundUser.get().getUsername());
        assertEquals("Arroz con leche", foundUser.get().getName());
        assertEquals("Balcarce 50", foundUser.get().getAddress());
    }

    @Test
    @DisplayName("Modificar usuario por nombre de usuario, de un usuario no existente. ")
    void updateUser_UserNotFound_GoodData() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        var request = new UserPatchEditRequest();
        request.setName("Arroz con leche");
        request.setAddress("Balcarce 50");
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.update("testUser", request);
        });
    }

    @Test
    @DisplayName("Modificar usuario por nombre de usuario, mal nombre. ")
    void updateUser_UserFound_BadData() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        var request = new UserPatchEditRequest();
        request.setName("Dulce d e l3che%%$");
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update("testUser", request);
        });
    }

    @Test
    @DisplayName("Modificar usuario autenticado, mal nombre. ")
    @WithMockUser(username = "testUser")
    void updateUser_CurrentUser_BadName() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Chocolate con albahaca");
        mockUser.setAddress("Alvear 5050");
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        var request = new UserPatchEditRequest();
        request.setName("Na na na na n4 b4tm4#");
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update(request);
        });
    }

    @Test
    @DisplayName("Modificar usuario por nombre de usuario, barrio inactivo. ")
    void updateUser_UserFound_BadNeighbourhood() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Neighbourhood inactiveMockFile = new Neighbourhood();
        inactiveMockFile.setId(666);
        inactiveMockFile.setName("Barrio inexistente");
        inactiveMockFile.setActive(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(666L)).thenReturn(Optional.of(inactiveMockFile));
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setNeighbourhood(inactiveMockFile);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update("testUser", request);
        });
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("Modificar usuario por nombre de usuario, barrio inactivo. ")
    void updateUser_CurrentUser_BadNeighbourhood() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Neighbourhood inactiveMockFile = new Neighbourhood();
        inactiveMockFile.setId(666);
        inactiveMockFile.setName("Barrio inexistente");
        inactiveMockFile.setActive(false);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(666L)).thenReturn(Optional.of(inactiveMockFile));
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setNeighbourhood(inactiveMockFile);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update(request);
        });
    }

    @Test
    @DisplayName("Modificar usuario por nombre de usuario, barrio inexistente. ")
    void updateUser_UserFound_NeighbourhoodNotFound() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Neighbourhood inactiveMockFile = new Neighbourhood();
        inactiveMockFile.setId(666);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(666L)).thenReturn(Optional.empty());
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setNeighbourhood(inactiveMockFile);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update("testUser", request);
        });
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("Modificar usuario autenticado, barrio inexistente. ")
    void updateUser_CurrentUser_NeighbourhoodNotFound() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Neighbourhood inactiveMockFile = new Neighbourhood();
        inactiveMockFile.setId(666);
        inactiveMockFile.setName("Barrio inexistente");
        inactiveMockFile.setActive(false);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(666L)).thenReturn(Optional.empty());
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setNeighbourhood(inactiveMockFile);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update(request);
        });
    }


    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("Modificar usuario autenticado, imagen inactiva. ")
    void updateUser_CurrentUser_BadPicture() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Picture inactivePicture = new Picture();
        UUID x = UUID.randomUUID();
        inactivePicture.setId(x);
        inactivePicture.setDescription("Barrio inexistente");
        inactivePicture.setActive(false);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(pictureRepository.findById(x)).thenReturn(Optional.of(inactivePicture));
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setPicture(inactivePicture);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update(request);
        });
    }
    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("Modificar usuario autenticado, imagen inexistente. ")
    void updateUser_CurrentUser_PictureNotFound() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Picture inactivePicture = new Picture();
        UUID x = UUID.randomUUID();
        inactivePicture.setId(x);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(pictureRepository.findById(x)).thenReturn(Optional.empty());
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setPicture(inactivePicture);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update(request);
        });
    }
    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("Modificar usuario autenticado, imagen de otra persona. ")
    void updateUser_CurrentUser_PictureNotOwner() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setId(8603);
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        User other = new User();
        other.setId(4500);
        other.setUsername("other.owner");
        other.setName("Otro");
        Picture inactivePicture = new Picture();
        UUID x = UUID.randomUUID();
        inactivePicture.setId(x);
        inactivePicture.setOwner(other);
        inactivePicture.setActive(true);
        when(authenticationService.getCurrentUserOrDie()).thenReturn(mockUser);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(pictureRepository.findById(x)).thenReturn(Optional.of(inactivePicture));
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setPicture(inactivePicture);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update(request);
        });
    }





    @Test
    @DisplayName("Modificar usuario por nombre de usuario, imagen inactiva. ")
    void updateUser_UserFound_BadPicture() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Picture inactivePicture = new Picture();
        UUID x = UUID.randomUUID();
        inactivePicture.setId(x);
        inactivePicture.setDescription("Barrio inexistente");
        inactivePicture.setActive(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(pictureRepository.findById(x)).thenReturn(Optional.of(inactivePicture));
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setPicture(inactivePicture);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update("testUser", request);
        });
    }
    @Test
    @DisplayName("Modificar usuario por nombre de usuario, imagen inexistente. ")
    void updateUser_UserFound_PictureNotFound() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        Picture inactivePicture = new Picture();
        UUID x = UUID.randomUUID();
        inactivePicture.setId(x);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(pictureRepository.findById(x)).thenReturn(Optional.empty());
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setPicture(inactivePicture);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update("testUser", request);
        });
    }
    @Test
    @DisplayName("Modificar usuario por nombre de usuario, imagen de otra persona. ")
    void updateUser_UserFound_PictureNotOwner() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setId(8603);
        mockUser.setUsername(username);
        mockUser.setName("Juan Manuel Rosario");
        User other = new User();
        other.setId(4500);
        other.setUsername("other.owner");
        other.setName("Otro");
        Picture inactivePicture = new Picture();
        UUID x = UUID.randomUUID();
        inactivePicture.setId(x);
        inactivePicture.setOwner(other);
        inactivePicture.setActive(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(pictureRepository.findById(x)).thenReturn(Optional.of(inactivePicture));
        var request = new UserPatchEditRequest();
        request.setName("Don Alberto Hermenegildo");
        request.setPicture(inactivePicture);
        request.setAddress("Balcarce 50");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            userService.update("testUser", request);
        });
    }

    @Test
    @DisplayName("Buscar usuario por nombre de usuario: Usuario que no existe. ")
    void findByUsername_UserNotFound_Throws() {
        String username = "unknownUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername(username));
    }

    @Test
    @DisplayName("Búsqueda simple. ")
    void testSearch() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> mockPage = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.search("user", pageable, true)).thenReturn(mockPage);

        Page<User> result = userService.search("user", pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("user1", result.getContent().get(0).getUsername());
        assertEquals("user2", result.getContent().get(1).getUsername());

        verify(userRepository, times(1)).search("user", pageable, true);
    }

    @Test
    void testSearchNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.search("nonexistent", pageable, true)).thenReturn(emptyPage);
        Page<User> result = userService.search("nonexistent", pageable);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).search("nonexistent", pageable, true);
    }


}
