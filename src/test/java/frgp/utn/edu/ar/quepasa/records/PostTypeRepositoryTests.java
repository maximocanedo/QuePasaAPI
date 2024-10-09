package frgp.utn.edu.ar.quepasa.records;

import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostTypeRepositoryTests {

    @Autowired private PostTypeRepository postTypeRepository;


    @Test
    @DisplayName("Alta, Borrado, Modificación y Lectura de tipos de publicaciones")
    public void crudTest() {
        PostType p = new PostType();
        Integer id = 1;
        p.setId(id);
        p.setDescription("Lalalala123");
        p.setActive(true);
        p = postTypeRepository.saveAndFlush(p);
        assertNotNull(p, "No guardó el tipo de publicación de prueba. ");
        assertTrue(postTypeRepository.findById(id).isPresent(), "No encuentra el tipo de publicación de prueba recién creado. ");
        p.setDescription("Lorem ipsum");
        assertNotNull(postTypeRepository.saveAndFlush(p), "No realizó la modificación. ");
        postTypeRepository.delete(p);
        assertTrue(postTypeRepository.findById(id).isEmpty(), "Encuentra un tipo de publicación físicamente borrado. ");
    }

}
