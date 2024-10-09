package frgp.utn.edu.ar.quepasa.records;

import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
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
public class PostSubtypeRepositoryTests {

    @Autowired
    private PostSubtypeRepository postSubtypeRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;


    @Test
    @DisplayName("Alta, Borrado, Modificación y Lectura de tipos de publicaciones")
    public void crudTest() {
        Integer tid = 1;
        PostType t = new PostType();
        t.setId(tid);
        t.setDescription("Asd456");
        t.setActive(true);
        t = postTypeRepository.saveAndFlush(t);
        assertNotNull(t, "No guardó el tipo de publicación de prueba. ");
        PostSubtype p = new PostSubtype();
        Integer pid = 1;
        p.setId(pid);
        p.setType(t);
        p.setDescription("Lalalala123");
        p.setActive(true);
        p = postSubtypeRepository.saveAndFlush(p);
        assertNotNull(p, "No guardó el subtipo de publicación de prueba. ");
        assertTrue(postSubtypeRepository.findById(pid).isPresent(), "No encuentra el subtipo de publicación de prueba recién creado. ");
        p.setDescription("Lorem ipsum");
        assertNotNull(postSubtypeRepository.saveAndFlush(p), "No realizó la modificación. ");
        postSubtypeRepository.delete(p);
        assertTrue(postSubtypeRepository.findById(pid).isEmpty(), "Encuentra un subtipo de publicación físicamente borrado. ");
    }
}
