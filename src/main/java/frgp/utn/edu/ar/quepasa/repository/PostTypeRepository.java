package frgp.utn.edu.ar.quepasa.repository;

import frgp.utn.edu.ar.quepasa.model.PostType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostTypeRepository extends JpaRepository<PostType, Integer> {
    @Query("SELECT p FROM PostType p WHERE (p.description LIKE %:query%) AND p.active = :active")
    Page<PostType> search(@NotNull String query, @NotNull Pageable pageable, boolean active);

    @Query("SELECT p FROM PostType p WHERE p.active = true")
    Page<PostType> findAllActive(Pageable pageable);

    @Query("SELECT p FROM PostType p WHERE p.id = :id AND p.active = true")
    Optional<PostType> findActiveById(int id);

    @Query("SELECT p FROM PostType p INNER JOIN PostSubtype s ON s.type = p WHERE s.id = :id AND p.active = true")
    Optional<PostType> findBySubtype(@Param("id") int id);

    @Query("SELECT p FROM PostType p INNER JOIN PostSubtype s ON s.type != p WHERE s.id = :id AND p.active = true ORDER BY p.id ASC")
    Page<PostType> findOthersBySubtype(@Param("id") int id, Pageable pageable);
}
