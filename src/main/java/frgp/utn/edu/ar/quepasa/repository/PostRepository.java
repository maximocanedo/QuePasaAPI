package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:query% OR p.description LIKE %:query% OR p.tags LIKE %:query%) AND p.active = :active")
    Page<Post> search(@NotNull String query, @NotNull Pageable pageable, boolean active);

    @Query("SELECT p FROM Post p WHERE p.active")
    Page<Post> findAllActive(Pageable pageable);

    Page<Post> findByOwner(User owner, Pageable pageable);
}
