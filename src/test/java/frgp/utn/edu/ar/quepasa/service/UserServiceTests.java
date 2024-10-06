package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
