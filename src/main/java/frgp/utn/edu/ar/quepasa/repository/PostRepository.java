package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByOwner(User owner, Pageable pageable);
}
