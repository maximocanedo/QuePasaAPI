package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.Post;
import frgp.utn.edu.ar.quepasa.model.User;
import frgp.utn.edu.ar.quepasa.model.enums.Audience;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:query% OR p.description LIKE %:query% OR p.tags LIKE %:query%) AND p.active = :active")
    Page<Post> search(@NotNull String query, @NotNull Pageable pageable, boolean active);

    @Query("SELECT p FROM Post p WHERE p.active")
    Page<Post> findAllActive(Pageable pageable);

    Page<Post> findByOwner(User owner, Pageable pageable);

    Page<Post> findByAudience(Audience audience, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.subtype.type.id = :type AND p.active")
    Page<Post> findByType(Integer type, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.subtype.id = :subtype AND p.active")
    Page<Post> findBySubtype(Integer subtype, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.timestamp BETWEEN :start AND :end")
    Page<Post> findByDateRange(Timestamp start, Timestamp end, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.timestamp > :start")
    Page<Post> findByDateStart(Timestamp start, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.timestamp < :end")
    Page<Post> findByDateEnd(Timestamp end, Pageable pageable);
}
