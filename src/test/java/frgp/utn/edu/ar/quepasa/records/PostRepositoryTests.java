package frgp.utn.edu.ar.quepasa.records;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.PostSubtype;
import frgp.utn.edu.ar.quepasa.model.PostType;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.model.geo.City;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.Neighbourhood;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.PostRepository;
import frgp.utn.edu.ar.quepasa.repository.PostSubtypeRepository;
import frgp.utn.edu.ar.quepasa.repository.PostTypeRepository;
import frgp.utn.edu.ar.quepasa.repository.UserRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CityRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.NeighbourhoodRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private PostSubtypeRepository postSubtypeRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private SubnationalDivisionRepository subnationalDivisionRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;

    @Test
    @DisplayName("Alta, Borrado, Modificación y Lectura de publicaciones")
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

        Country c = new Country();
        c.setIso3("AUT");
        c.setLabel("Nowhere Country");
        c.setActive(true);
        c = countryRepository.saveAndFlush(c);
        assertNotNull(c, "No guardó el país de prueba. ");

        SubnationalDivision s = new SubnationalDivision();
        s.setIso3("US-TX");
        s.setLabel("Nowhere Subdivision");
        s.setCountry(c);
        s.setActive(true);
        s = subnationalDivisionRepository.saveAndFlush(s);
        assertNotNull(s, "No guardó la subdivisión nacional de prueba. ");

        City ct = new City();
        long cid = 1L;
        ct.setId(cid);
        ct.setName("Nowhere City");
        ct.setSubdivision(s);
        ct.setActive(true);
        ct = cityRepository.saveAndFlush(ct);
        assertNotNull(ct, "No guardó la ciudad de prueba. ");

        Neighbourhood n = new Neighbourhood();
        long nid = 1L;
        n.setId(nid);
        n.setName("Ningún Lugar");
        n.setCity(ct);
        n.setActive(true);
        n = neighbourhoodRepository.saveAndFlush(n);
        assertNotNull(n, "No guardó el barrio de prueba. ");

        Integer uid = 1;
        User u = new User();
        u.setId(uid);
        u.setUsername("gogo15");
        u.setName("tiki40");
        u.setAddress("nowherestreet123");
        u.setNeighbourhood(n);
        u.setPassword("pass123");
        u.setRole(Role.CONTRIBUTOR);
        u = userRepository.saveAndFlush(u);
        assertNotNull(u, "No guardó el usuario de prueba. ");

        Integer id = 1;
        Post post = new Post();
        post.setId(id);
        post.setOwner(u);
        post.setAudience(Audience.NEIGHBORHOOD);
        post.setTitle("Post");
        post.setSubtype(p);
        post.setDescription("Lalalala123");
        post.setNeighbourhood(n);
        post.setTimestamp(Timestamp.valueOf("2024-10-08 14:30:15.123456789"));
        post.setTags("asd,donald,123");
        post.setActive(true);

        post = postRepository.saveAndFlush(post);
        assertNotNull(post, "No guardó la publicación. ");
        assertTrue(postRepository.findById(id).isPresent(), "No encuentra la publicación recién creada. ");
        post.setDescription("Lorem ipsum");
        assertNotNull(postRepository.saveAndFlush(post), "No realizó la modificación. ");
        postRepository.delete(post);
        assertTrue(postRepository.findById(id).isEmpty(), "Encuentra una publicación físicamente borrada. ");
    }
}
